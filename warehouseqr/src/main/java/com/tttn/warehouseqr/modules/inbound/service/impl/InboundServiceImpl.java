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
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseLocationRepository;
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

    public InboundServiceImpl(InboundReceiptRepository receiptRepo, InboundReceiptItemRepository itemRepo, PurchaseOrderItemRepository poItemRepo, InventoryLocationBalanceRepository balanceRepo, InventoryHistoryRepository historyRepo, ProductRepository productRepo, ProductBatchRepository batchRepo, WarehouseLocationRepository warehouseLocationRepository) {
        this.receiptRepo = receiptRepo;
        this.itemRepo = itemRepo;
        this.poItemRepo = poItemRepo;
        this.balanceRepo = balanceRepo;
        this.historyRepo = historyRepo;
        this.productRepo = productRepo;
        this.batchRepo = batchRepo;
        this.warehouseLocationRepository = warehouseLocationRepository;
    }


    @Override
    @Transactional(rollbackFor = Exception.class) // Đã thêm rollback an toàn
    public InboundReceipt createInboundReceipt(InboundRequestDTO dto) {
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

        InboundReceipt savedReceipt = receiptRepo.save(receipt);

        // 2. Duyệt Items
        for (var itemDto : dto.getItems()) {
            if (itemDto.getProductId() == null) throw new RuntimeException("Lỗi: productId bị null");
            if (itemDto.getLocationId() == null) throw new RuntimeException("Lỗi: locationId bị null");

            InboundReceiptItem item = new InboundReceiptItem();
            item.setInboundReceipt(savedReceipt);

            var product = productRepo.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không thấy SP: " + itemDto.getProductId()));
            item.setProduct(product);

            if (itemDto.getBatchId() != null) {
                var batch = batchRepo.findById(itemDto.getBatchId())
                        .orElseThrow(() -> new RuntimeException("Không thấy lô: " + itemDto.getBatchId()));
                item.setBatch(batch);
            }

            BigDecimal qty = BigDecimal.valueOf(itemDto.getActualQty() != null ? itemDto.getActualQty() : 0.0);
            item.setActualQty(qty);
            item.setExpectedQty(qty);
            item.setPutawayLocationId(itemDto.getLocationId());

            itemRepo.save(item);

            // QUAN TRỌNG: Chỉ update PO nếu có PurchaseOrderId
            if (dto.getPurchaseOrderId() != null) {
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
                // NẾU ĐÃ CÓ HÀNG TẠI VỊ TRÍ NÀY -> Cộng dồn
                InventoryLocationBalance existingBalance = balanceOpt.get();
                existingBalance.setQty(existingBalance.getQty().add(qty));
                existingBalance.setUpdateAt(LocalDateTime.now()); // Cập nhật thời gian
                balanceRepo.save(existingBalance);
            } else {
                // NẾU CHƯA CÓ -> Tạo dòng mới tại vị trí này
                InventoryLocationBalance newBalance = new InventoryLocationBalance();
                newBalance.setWarehouseId(dto.getWarehouseId());
                newBalance.setLocationId(itemDto.getLocationId());
                newBalance.setProductId(itemDto.getProductId());
                newBalance.setBatchId(itemDto.getBatchId());
                newBalance.setQty(qty);
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
            history.setBatchId(itemDto.getBatchId()); // ĐÃ THÊM: Lưu Batch ID vào lịch sử
            history.setQtyChange(qty);
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
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            reader.mark(1);
            if (reader.read() != 0xFEFF) {
                reader.reset();
            }

            CSVParser parser = new CSVParser(reader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            for (CSVRecord record : parser) {
                String sku = record.get("SKU");
                String lotCode = record.get("Mã Lô Hàng");
                String qtyStr = record.get("Số Lượng");
                String locationCode = record.get("Mã Vị Trí");

                Product product = productRepo.findBySku(sku);

                var batch = batchRepo.findByLotCodeAndProductProduct_id(lotCode, product.getProduct_id()).orElse(null);

                var location = warehouseLocationRepository.findByLocationCode(locationCode).orElseThrow(() -> new RuntimeException("Vị trí đặt không tồn : " + locationCode));

                ProductScanDTO dto = new ProductScanDTO();
                dto.setProductId(product.getProduct_id());
                dto.setProductName(product.getProductName());
                dto.setSku(sku);
                dto.setLotCode(lotCode);
                dto.setBatchId(batch != null ? batch.getBatchId(): null);
                dto.setActualQty(Double.parseDouble(qtyStr));
                dto.setLocationId(location.getLocationId());
                dto.setLocationCode(locationCode);

                dtos.add(dto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file: " + e.getMessage());
        }
        return dtos;
    }
}