package com.tttn.warehouseqr.modules.salesorder.service;

import com.tttn.warehouseqr.modules.masterdata.customer.entity.Customer;
import com.tttn.warehouseqr.modules.masterdata.customer.repository.CustomerRepository;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.salesorder.entity.SalesOrder;
import com.tttn.warehouseqr.modules.salesorder.entity.SalesOrderItem;
import com.tttn.warehouseqr.modules.salesorder.repository.SalesOrderRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@SuppressWarnings("unused")
public class SalesOrderImportService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public SalesOrderImportService(SalesOrderRepository salesOrderRepository,
                                   CustomerRepository customerRepository,
                                   ProductRepository productRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @SuppressWarnings("unused")
    @Transactional(rollbackFor = Exception.class)
    public void createSalesOrderFromCsv(Long customerId, MultipartFile file) {
        if (customerId == null) {
            throw new RuntimeException("Vui lòng chọn khách hàng");
        }
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn file CSV");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng có ID: " + customerId));

        SalesOrder order = new SalesOrder();
        order.setSoCode("SO-" + System.currentTimeMillis());
        order.setCustomerId(customer.getCustomerId());
        order.setCustomerName(customer.getCustomerName());
        order.setStatus("DRAFT");
        order.setPaymentStatus("UNPAID");
        order.setPaymentMethod(null);
        order.setPaymentTransactionCode(null);
        order.setPaymentNote(null);
        order.setPaymentConfirmedAt(null);
        order.setPaymentConfirmedBy(null);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setTotalAmount(BigDecimal.ZERO);
        order.setItems(new ArrayList<>());

        BigDecimal totalAmount = BigDecimal.ZERO;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.DEFAULT.builder()
                             .setHeader()
                             .setSkipHeaderRecord(true)
                             .setIgnoreHeaderCase(true)
                             .setTrim(true)
                             .build())) {

            Map<String, Integer> headerMap = csvParser.getHeaderMap();
            int skuIdx = resolveHeaderIndex(headerMap, "SKU", "Mã SP", "Ma SP", "Product SKU", "ProductSKU", "SKU Code");
            int qtyIdx = resolveHeaderIndex(headerMap, "Quantity", "Qty", "Số Lượng", "So Luong", "OrderedQty", "SoLuong");
            int unitPriceIdx = resolveHeaderIndex(headerMap, "UnitPrice", "Price", "Đơn Giá", "Don Gia", "Giá Bán", "Gia Ban", "SalePrice");

            if (skuIdx < 0 || qtyIdx < 0 || unitPriceIdx < 0) {
                throw new RuntimeException("Thiếu cột bắt buộc. Cần tối thiểu các cột SKU, Quantity(Qty), UnitPrice(Price). Thứ tự cột có thể thay đổi.");
            }

            for (CSVRecord record : csvParser) {
                String sku = getValueByIndex(record, skuIdx);
                if (sku == null || sku.isBlank()) {
                    continue;
                }

                String qtyRaw = getValueByIndex(record, qtyIdx);
                String unitPriceRaw = getValueByIndex(record, unitPriceIdx);

                BigDecimal quantity = parseDecimal(qtyRaw, "số lượng");
                if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("Số lượng phải lớn hơn 0 cho SKU: " + sku);
                }

                BigDecimal unitPrice = parseDecimal(unitPriceRaw, "đơn giá");
                if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("Đơn giá không được âm cho SKU: " + sku);
                }

                Product product = productRepository.findBySku(sku);
                if (product == null) {
                    throw new RuntimeException("Không tìm thấy sản phẩm theo SKU: " + sku);
                }

                SalesOrderItem item = new SalesOrderItem();
                item.setSalesOrder(order);
                item.setProductId(product.getProduct_id());
                item.setQuantity(quantity);
                item.setShippedQty(BigDecimal.ZERO);
                item.setUnitPrice(unitPrice);
                order.getItems().add(item);

                totalAmount = totalAmount.add(quantity.multiply(unitPrice));
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi xử lý file CSV: " + e.getMessage());
        }

        if (order.getItems().isEmpty()) {
            throw new RuntimeException("File CSV không có dữ liệu hợp lệ");
        }

        order.setTotalAmount(totalAmount);
        order.setUpdatedAt(LocalDateTime.now());
        salesOrderRepository.save(order);
    }

    private int resolveHeaderIndex(Map<String, Integer> headerMap, String... aliases) {
        if (headerMap == null || headerMap.isEmpty()) {
            return -1;
        }

        List<String> normalizedAliases = Arrays.stream(aliases)
                .filter(a -> a != null && !a.isBlank())
                .map(this::normalizeHeader)
                .toList();

        for (Map.Entry<String, Integer> headerEntry : headerMap.entrySet()) {
            String rawHeader = headerEntry.getKey();
            if (rawHeader == null) {
                continue;
            }
            String normalized = normalizeHeader(rawHeader.replace("\uFEFF", ""));
            if (normalizedAliases.contains(normalized)) {
                return headerEntry.getValue();
            }
        }
        return -1;
    }

    private String normalizeHeader(String raw) {
        String v = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        v = v.replace('đ', 'd');
        v = v.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        v = v.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        v = v.replaceAll("[ìíịỉĩ]", "i");
        v = v.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        v = v.replaceAll("[ùúụủũưừứựửữ]", "u");
        v = v.replaceAll("[ỳýỵỷỹ]", "y");
        v = v.replaceAll("[^a-z0-9]", "");
        return v;
    }

    private String getValueByIndex(CSVRecord record, int index) {
        if (index < 0 || index >= record.size()) {
            return null;
        }
        String value = record.get(index);
        return value != null ? value.trim() : null;
    }

    private BigDecimal parseDecimal(String raw, String label) {
        if (raw == null || raw.isBlank()) {
            throw new RuntimeException("Thiếu " + label);
        }
        try {
            return new BigDecimal(raw.trim());
        } catch (Exception e) {
            throw new RuntimeException("Giá trị " + label + " không hợp lệ: " + raw);
        }
    }
}

