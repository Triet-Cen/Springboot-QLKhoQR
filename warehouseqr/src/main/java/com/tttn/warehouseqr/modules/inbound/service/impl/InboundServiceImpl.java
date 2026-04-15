package com.tttn.warehouseqr.modules.inbound.service.impl;

import com.tttn.warehouseqr.modules.inbound.dto.InboundRequestDTO;
import com.tttn.warehouseqr.modules.inbound.entity.InboundReceipt;
import com.tttn.warehouseqr.modules.inbound.entity.InboundReceiptItem;
import com.tttn.warehouseqr.modules.inbound.repository.InboundReceiptItemRepository;
import com.tttn.warehouseqr.modules.inbound.repository.InboundReceiptRepository;
import com.tttn.warehouseqr.modules.inbound.service.InboundService;
import com.tttn.warehouseqr.modules.inventory.entity.InventoryHistory;
import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance; // ĐÃ THÊM IMPORT NÀY
import com.tttn.warehouseqr.modules.inventory.repository.InventoryHistoryRepository;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductScanDTO;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.supplier.repository.SupplierRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseLocationRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseRepository;
import com.tttn.warehouseqr.modules.purchase.repository.PurchaseOrderItemRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InboundServiceImpl implements InboundService {
    private final InboundReceiptRepository receiptRepo;
    private final InboundReceiptItemRepository itemRepo;

    private final PurchaseOrderItemRepository poItemRepo;
    private final InventoryLocationBalanceRepository balanceRepo;
    private final InventoryHistoryRepository historyRepo;

    private final ProductRepository productRepo;
    private final ProductBatchRepository batchRepo;

    private final WarehouseLocationRepository warehouseLocationRepository;

    private final SupplierRepository supplierRepository;

    private final WarehouseRepository warehouseRepository;

    public InboundServiceImpl(InboundReceiptRepository receiptRepo, InboundReceiptItemRepository itemRepo, PurchaseOrderItemRepository poItemRepo, InventoryLocationBalanceRepository balanceRepo, InventoryHistoryRepository historyRepo, ProductRepository productRepo, ProductBatchRepository batchRepo, WarehouseLocationRepository warehouseLocationRepository, SupplierRepository supplierRepository, WarehouseRepository warehouseRepository) {
        this.receiptRepo = receiptRepo;
        this.itemRepo = itemRepo;
        this.poItemRepo = poItemRepo;
        this.balanceRepo = balanceRepo;
        this.historyRepo = historyRepo;
        this.productRepo = productRepo;
        this.batchRepo = batchRepo;
        this.warehouseLocationRepository = warehouseLocationRepository;
        this.supplierRepository = supplierRepository;
        this.warehouseRepository = warehouseRepository;
    }


    @Override
    @Transactional(rollbackFor = Exception.class) // Đã thêm rollback an toàn
    public InboundReceipt createInboundReceipt(InboundRequestDTO dto, Long userId) {
        // 1. Kiểm tra Header cơ bản
        if (dto.getWarehouseId() == null) throw new RuntimeException("Lỗi: warehouseId bị null");

        InboundReceipt receipt = new InboundReceipt();
        // Tạo mã phiếu tự động nếu FE không gửi
        receipt.setInboundReceiptCode(dto.getInboundReceiptCode() != null ?
                dto.getInboundReceiptCode() : "PN-" + System.currentTimeMillis());

        receipt.setPurchaseOrderId(dto.getPurchaseOrderId()); // Có thể null khi quét lẻ
        receipt.setSupplierId(dto.getSupplierId());
        receipt.setWarehouseId(dto.getWarehouseId());
        receipt.setStatus("COMPLETED");
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setReceivedAt(LocalDateTime.now());
        receipt.setCreatedBy(userId);

        InboundReceipt savedReceipt = receiptRepo.save(receipt);

        // 2. Duyệt Items
        for (var itemDto : dto.getItems()) {
            if (itemDto.getProductId() == null) throw new RuntimeException("Lỗi: productId bị null");
            if (itemDto.getLocationId() == null) throw new RuntimeException("Lỗi: locationId bị null");

            InboundReceiptItem item = new InboundReceiptItem();
            item.setInboundReceipt(savedReceipt);

            if (itemDto.getImportPrice() != null) {
                item.setImportPrice(BigDecimal.valueOf(itemDto.getImportPrice()));
            } else {
                item.setImportPrice(BigDecimal.ZERO);
            }

            var product = productRepo.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không thấy SP: " + itemDto.getProductId()));
            item.setProduct(product);

            if (itemDto.getBatchId() != null) {
                var batch = batchRepo.findById(itemDto.getBatchId())
                        .orElseThrow(() -> new RuntimeException("Không thấy lô: " + itemDto.getBatchId()));
                item.setBatch(batch);
            }

            BigDecimal actualQty = BigDecimal.valueOf(itemDto.getActualQty() != null ? itemDto.getActualQty() : 0.0);
            BigDecimal expectedQty = BigDecimal.valueOf(itemDto.getExpectedQty() != null ? itemDto.getExpectedQty() : 0.0);

            item.setActualQty(actualQty);
            item.setExpectedQty(expectedQty); // Phải lấy từ trường expectedQty của DTO
            item.setPutawayLocationId(itemDto.getLocationId());


            itemRepo.save(item);

            // QUAN TRỌNG: Chỉ update PO nếu có PurchaseOrderId
            if (dto.getPurchaseOrderId() != null) {
                // Cập nhật số lượng thực nhận vào đơn mua
                poItemRepo.updateReceivedQty(dto.getPurchaseOrderId(), itemDto.getProductId(), itemDto.getActualQty());
            }

            // =================================================================
            // Cập nhật tồn kho (Kiểm tra xem có chưa mới cộng dồn)
            // =================================================================
            var balanceOpt = balanceRepo.findByWarehouseIdAndLocationIdAndProductIdAndBatchId(
                    dto.getWarehouseId(),
                    itemDto.getLocationId(),
                    itemDto.getProductId(),
                    itemDto.getBatchId()
            );

            if (balanceOpt.isPresent()) {
                InventoryLocationBalance existingBalance = balanceOpt.get();
                existingBalance.setQty(existingBalance.getQty().add(actualQty));
                existingBalance.setUpdateAt(LocalDateTime.now()); // Cập nhật thời gian
                balanceRepo.save(existingBalance);
            } else {
                // NẾU CHƯA CÓ -> Tạo dòng mới tại vị trí này
                InventoryLocationBalance newBalance = new InventoryLocationBalance();
                newBalance.setWarehouseId(dto.getWarehouseId());
                newBalance.setLocationId(itemDto.getLocationId());
                newBalance.setProductId(itemDto.getProductId());
                newBalance.setBatchId(itemDto.getBatchId());
                newBalance.setQty(actualQty);
                newBalance.setStatus("AVAILABLE");
                // THÊM DÒNG NÀY: Ghi lại thời gian tạo
                newBalance.setUpdateAt(LocalDateTime.now());
                balanceRepo.save(newBalance);
            }
            // =================================================================

            // Ghi lịch sử kho
            InventoryHistory history = new InventoryHistory();
            history.setTransactionType("INBOUND");
            history.setProductId(itemDto.getProductId());
            history.setBatchId(itemDto.getBatchId());
            history.setQtyChange(actualQty);
            history.setToLocationId(itemDto.getLocationId());
            history.setWarehouseId(dto.getWarehouseId());
            historyRepo.save(history);
        }
        return savedReceipt;
    }

    @Override
    public InboundReceipt getById(Long id) {
        return receiptRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập"));
    }

    @Override
    public List<ProductScanDTO> parseCsvToDTO(MultipartFile file) {
        List<ProductScanDTO> dtos = new ArrayList<>();

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");


        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            reader.mark(1);
            if (reader.read() != 0xFEFF) {
                reader.reset();
            }

            CSVParser parser = new CSVParser(reader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            for (CSVRecord record : parser) {
                // Đọc dữ liệu từ các cột trong CSV
                String sku = record.get("SKU");
                String lotCode = record.get("Mã Lô Hàng");
                String expiryDateStr = record.get("Ngày Hết Hạn");
                String supplierCode = record.get("Mã NCC");
                String qtyStr = record.get("Số Lượng"); // Số lượng này đóng vai trò là "Dự kiến" từ hóa đơn
                String priceStr = record.isMapped("Giá Nhập") ? record.get("Giá Nhập") : "0";
                String warehouseCode = record.isMapped("Mã Kho") ? record.get("Mã Kho") : "";



                // 1. Kiểm tra Sản phẩm (Master Data)
                Product product = productRepo.findBySku(sku);
                if (product == null) {
                    throw new RuntimeException("Lỗi dòng " + record.getRecordNumber() + ": Sản phẩm SKU " + sku + " chưa tồn tại!");
                }


                // 2. Xử lý Lô hàng (ProductBatch) - Tự động tạo định danh để in tem QR
                var batch = batchRepo.findByLotCodeAndProductProduct_id(lotCode, product.getProduct_id())
                        .orElseGet(() -> {
                            ProductBatch newBatch = new ProductBatch();
                            newBatch.setLotCode(lotCode);
                            newBatch.setProduct(product);
                            newBatch.setCostPrice(new BigDecimal(priceStr));

                            // Xử lý ngày hết hạn
                            if (expiryDateStr != null && !expiryDateStr.trim().isEmpty()) {
                                try {
                                    newBatch.setExpiryDate(java.time.LocalDate.parse(expiryDateStr.trim(), formatter));
                                } catch (Exception e) {
                                    throw new RuntimeException("Lỗi định dạng ngày tại dòng " + record.getRecordNumber() + ": " + expiryDateStr);
                                }
                            }

                            // Gán Nhà cung cấp từ SupplierRepository
                            if (supplierCode != null && !supplierCode.trim().isEmpty()) {
                                supplierRepository.findBySupplierCode(supplierCode.trim())
                                        .ifPresent(newBatch::setSupplier);
                            }

                            // Lưu ngay để lấy Batch ID phục vụ in tem QR
                            return batchRepo.save(newBatch);
                        });

                // 3. Xác định Vị trí kho (Put-away Location)
                String locationCode = (record.isMapped("Mã Vị Trí") && !record.get("Mã Vị Trí").isEmpty())
                        ? record.get("Mã Vị Trí") : "LOC-DEFAULT-01";

                var location = warehouseLocationRepository.findByLocationCode(locationCode)
                        .orElseThrow(() -> new RuntimeException("Vị trí " + locationCode + " không tồn tại trong hệ thống!"));

                // 4. Đóng gói DTO (Áp dụng logic đối soát Phương án A)
                double qtyFromCsv = Double.parseDouble(qtyStr);

                ProductScanDTO dto = new ProductScanDTO();
                dto.setProductId(product.getProduct_id());
                dto.setProductName(product.getProductName());
                dto.setSku(sku);
                dto.setLotCode(lotCode);
                dto.setBatchId(batch.getBatchId()); // Batch ID dùng để sinh mã QR

                // LOGIC QUAN TRỌNG:
                dto.setExpectedQty(qtyFromCsv); // Số lượng dự kiến từ hóa đơn/CSV (không cho sửa ở FE)
                dto.setActualQty(qtyFromCsv);   // Số lượng thực tế (Ban đầu bằng dự kiến, cho FE sửa)

                dto.setLocationId(location.getLocationId());
                dto.setLocationCode(locationCode);
                dto.setImportPrice(Double.parseDouble(priceStr));

                // Tìm warehouseId từ mã kho
                if (!warehouseCode.isEmpty()) {
                    warehouseRepository.findByWarehouseCode(warehouseCode)
                            .ifPresent(w -> dto.setWarehouseId(w.getWarehouseId()));
                }

                // Tìm supplierId từ mã NCC (bạn đã có supplierCode)
                if (supplierCode != null) {
                    supplierRepository.findBySupplierCode(supplierCode)
                            .ifPresent(s -> dto.setSupplierId(s.getSupplierId()));
                }

                dtos.add(dto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file: " + e.getMessage());
        }
        return dtos;
    }
}