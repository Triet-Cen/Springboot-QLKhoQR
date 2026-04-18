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
import java.util.ArrayList;
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
                productBatchRepo.findById(item.getBatchId()).ifPresent(batch -> {
                    dto.setLotCode(batch.getLotCode());
                });
            } else {
                // Nếu item không có batchId -> Chắc chắn là file CSV lúc tạo PO không có cột Mã Lô
                dto.setLotCode("Chờ nhập");
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrders createPoFromCsv(MultipartFile file, Long supplierId, Long warehouseId, Long userId) {
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
        return poRepo.save(po);
    }
}
