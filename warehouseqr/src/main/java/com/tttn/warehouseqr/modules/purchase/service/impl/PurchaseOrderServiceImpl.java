package com.tttn.warehouseqr.modules.purchase.service.impl;

import com.tttn.warehouseqr.modules.auth.entity.User;
import com.tttn.warehouseqr.modules.inbound.request.InboundItemRequestDTO;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import com.tttn.warehouseqr.modules.purchase.entity.PurchaseOrderItem;
import com.tttn.warehouseqr.modules.purchase.entity.PurchaseOrders;
import com.tttn.warehouseqr.modules.purchase.repository.PurchaseOrderItemRepository;
import com.tttn.warehouseqr.modules.purchase.repository.PurchaseOrdersRepository;
import com.tttn.warehouseqr.modules.purchase.service.PurchaseOrderService;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderItemRepository poItemRepo;

    private final ProductRepository productRepo;

    private final ProductBatchRepository productBatchRepo;

    private final PurchaseOrdersRepository poRepo;

    public PurchaseOrderServiceImpl(PurchaseOrderItemRepository poItemRepo, ProductRepository productRepo, ProductBatchRepository productBatchRepo, PurchaseOrdersRepository poRepo) {
        this.poItemRepo = poItemRepo;
        this.productRepo = productRepo;
        this.productBatchRepo = productBatchRepo;
        this.poRepo = poRepo;
    }

    @Override
    public List<InboundItemRequestDTO> getItemsByPoId(Long poId) {
        List<PurchaseOrderItem> items = poItemRepo.findByPurchaseOrders_Id(poId);

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Đơn hàng #" + poId + " trống hoặc không tồn tại!");
        }

        return items.stream().map(item -> {
            InboundItemRequestDTO dto = new InboundItemRequestDTO();

            Product product = item.getProduct();
            if (product != null) {
                dto.setProductId(product.getProduct_id());
                dto.setProductName(product.getProductName());
            } else {
                dto.setProductName("Sản phẩm không xác định");
            }

            dto.setExpectedQty(item.getOrderedQty() != null ? item.getOrderedQty().doubleValue() : 0.0);
            dto.setImportPrice(item.getUnitPrice() != null ? item.getUnitPrice().doubleValue() : 0.0);
            dto.setActualQty(0.0);

            dto.setBatchId(item.getBatchId());
            dto.setLocationId(null);

            // CÁCH HIỂN THỊ MÃ LÔ ĐÃ ĐƯỢC LÀM SẠCH:
            if (item.getBatchId() != null) {
                productBatchRepo.findById(item.getBatchId()).ifPresent(batch -> dto.setLotCode(batch.getLotCode()));
            } else {
                // Nếu item không có batchId -> Chắc chắn là file CSV lúc tạo PO không có cột Mã Lô
                dto.setLotCode("Chờ nhập");
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPoFromCsv(MultipartFile file, Long supplierId, Long warehouseId, Long userId) {
        // 1. Khởi tạo PO trong bộ nhớ (Chưa lưu DB ngay)
        PurchaseOrders po = new PurchaseOrders();
        po.setPoCode("PO-" + System.currentTimeMillis());
        if(supplierId != null)
        {
            Supplier supplier = new Supplier();
            supplier.setSupplierId(supplierId);
            po.setSupplier(supplier);
        }
        if (warehouseId != null)
        {
            Warehouse warehouse = new Warehouse();
            warehouse.setWarehouseId(warehouseId);
            po.setWarehouse(warehouse);
        }

        po.setStatus("DRAFT");
        po.setOrderDate(LocalDateTime.now()); // <--- THÊM DÒNG NÀY
        if(userId != null)
        {
            User user = new User();
            user.setUserId(userId);
            po.setCreatedBy(user);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.builder()
                             .setHeader()
                             .setSkipHeaderRecord(true)
                             .setIgnoreHeaderCase(true)
                             .setTrim(true)
                             .build())) {

            for (CSVRecord csvRecord : csvParser) {
                String sku = csvRecord.get("SKU");
                BigDecimal expectedQty = new BigDecimal(csvRecord.get("ExpectedQty"));
                BigDecimal unitPrice = new BigDecimal(csvRecord.get("UnitPrice"));

                Product product = Optional.ofNullable(productRepo.findBySku(sku))
                        .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy SKU [" + sku + "]"));

                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setProduct(product);
                item.setOrderedQty(expectedQty);
                item.setUnitPrice(unitPrice);
                item.setReceivedQty(BigDecimal.ZERO);

                // ==========================================
                // CÁCH XỬ LÝ MÃ LÔ MỚI (Dùng đúng trường batchId của bạn)
                // ==========================================
                if (csvRecord.isMapped("Mã Lô Hàng") && !csvRecord.get("Mã Lô Hàng").trim().isEmpty()) {
                    String lotCodeStr = csvRecord.get("Mã Lô Hàng").trim();

                    // Đi tìm Lô hàng này trong DB. Nếu chưa có thì tự động TẠO MỚI luôn.
                    // (Sử dụng lại đúng hàm bạn đã viết bên InboundServiceImpl)
                    var batch = productBatchRepo.findByLotCodeAndProductProduct_id(lotCodeStr, product.getProduct_id())
                            .orElseGet(() -> {
                                com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch newBatch = new com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch();
                                newBatch.setLotCode(lotCodeStr);
                                newBatch.setProduct(product);
                                // Có thể set thêm giá costPrice nếu cần
                                return productBatchRepo.save(newBatch);
                            });

                    // Gán đúng cái ID của Lô hàng vào Entity của bạn
                    item.setBatchId(batch.getBatchId());
                } else {
                    // Nếu file CSV không có cột Mã Lô thì để null
                    item.setBatchId(null);
                }
                // ==========================================

                po.addItem(item);
                totalAmount = totalAmount.add(unitPrice.multiply(expectedQty));
            }
        } catch (IOException | NumberFormatException | ArithmeticException e) {
            throw new RuntimeException("Lỗi xử lý file CSV: " + e.getMessage());
        }

        if (po.getItems().isEmpty()) {
            throw new RuntimeException("File CSV không có dữ liệu hợp lệ!");
        }

        po.setTotalAmount(totalAmount);

        // 2. LƯU DUY NHẤT 1 LẦN: Hibernate tự động INSERT PO trước, lấy ID, rồi INSERT toàn bộ Items
        poRepo.save(po);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createManualPurchaseOrder(Long supplierId,
                                          Long warehouseId,
                                          LocalDateTime expectedDeliveryDate,
                                          String notes,
                                          List<String> productIds,
                                          List<String> orderedQtys,
                                          List<String> unitPrices,
                                          List<String> batchIds,
                                          Long userId) {
        if (supplierId == null) {
            throw new RuntimeException("Vui lòng chọn nhà cung cấp");
        }
        if (warehouseId == null) {
            throw new RuntimeException("Vui lòng chọn kho nhận hàng");
        }

        PurchaseOrders po = new PurchaseOrders();
        po.setPoCode("PO-" + System.currentTimeMillis());

        Supplier supplier = new Supplier();
        supplier.setSupplierId(supplierId);
        po.setSupplier(supplier);

        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseId(warehouseId);
        po.setWarehouse(warehouse);

        if (userId != null) {
            User user = new User();
            user.setUserId(userId);
            po.setCreatedBy(user);
        }

        po.setOrderDate(LocalDateTime.now());
        po.setExpectedDeliveryDate(expectedDeliveryDate);
        po.setNotes(notes);
        po.setStatus("DRAFT");

        int rowCount = Math.max(
                Math.max(productIds != null ? productIds.size() : 0, orderedQtys != null ? orderedQtys.size() : 0),
                Math.max(unitPrices != null ? unitPrices.size() : 0, batchIds != null ? batchIds.size() : 0)
        );

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (int i = 0; i < rowCount; i++) {
            String productIdRaw = safeGet(productIds, i);
            String qtyRaw = safeGet(orderedQtys, i);
            String priceRaw = safeGet(unitPrices, i);
            String batchIdRaw = safeGet(batchIds, i);

            if (productIdRaw == null || productIdRaw.trim().isEmpty()) {
                continue;
            }

            Long productId = parseLongOrNull(productIdRaw);
            if (productId == null) {
                throw new RuntimeException("Dòng " + (i + 1) + ": mã sản phẩm không hợp lệ");
            }

            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + productId));

            BigDecimal orderedQty = parseOrderedQty(qtyRaw);
            if (orderedQty.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Dòng " + (i + 1) + ": số lượng phải lớn hơn 0");
            }

            BigDecimal unitPrice = parseUnitPrice(priceRaw);
            if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Dòng " + (i + 1) + ": đơn giá không được âm");
            }

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setProduct(product);
            item.setOrderedQty(orderedQty);
            item.setReceivedQty(BigDecimal.ZERO);
            item.setUnitPrice(unitPrice);
            item.setBatchId(parseLongOrNull(batchIdRaw));

            po.addItem(item);
            totalAmount = totalAmount.add(unitPrice.multiply(orderedQty));
        }

        if (po.getItems().isEmpty()) {
            throw new RuntimeException("Vui lòng thêm ít nhất một sản phẩm hợp lệ");
        }

        po.setTotalAmount(totalAmount);
        poRepo.save(po);
    }

    private String safeGet(List<String> values, int index) {
        if (values == null || index < 0 || index >= values.size()) {
            return null;
        }
        return values.get(index);
    }

    private Long parseLongOrNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseOrderedQty(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException("Thiếu số lượng đặt");
        }
        try {
            return new BigDecimal(value.trim());
        } catch (Exception e) {
            throw new RuntimeException("Giá trị số lượng đặt không hợp lệ: " + value);
        }
    }

    private BigDecimal parseUnitPrice(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
