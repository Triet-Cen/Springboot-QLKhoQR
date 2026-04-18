package com.tttn.warehouseqr.modules.inbound.service.impl;

import com.tttn.warehouseqr.modules.auth.entity.User;
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
import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;
import com.tttn.warehouseqr.modules.masterdata.supplier.repository.SupplierRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseLocationRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseRepository;
import com.tttn.warehouseqr.modules.purchase.entity.PurchaseOrders;
import com.tttn.warehouseqr.modules.purchase.repository.PurchaseOrderItemRepository;
import com.tttn.warehouseqr.modules.purchase.repository.PurchaseOrdersRepository;
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

    private final PurchaseOrdersRepository poRepository;

    public InboundServiceImpl(InboundReceiptRepository receiptRepo, InboundReceiptItemRepository itemRepo, PurchaseOrderItemRepository poItemRepo, InventoryLocationBalanceRepository balanceRepo, InventoryHistoryRepository historyRepo, ProductRepository productRepo, ProductBatchRepository batchRepo, WarehouseLocationRepository warehouseLocationRepository, SupplierRepository supplierRepository, WarehouseRepository warehouseRepository, PurchaseOrdersRepository poRepository) {
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
        this.poRepository = poRepository;
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

        if(dto.getPurchaseOrderId() != null)
        {
            PurchaseOrders po = new PurchaseOrders();
            po.setId(dto.getPurchaseOrderId());
            receipt.setPurchaseOrders(po);
        }
        if (dto.getSupplierId() != null)
        {
            Supplier supplier = new Supplier();
            supplier.setSupplierId(dto.getSupplierId());
            receipt.setSupplier(supplier);
        }

        if(dto.getWarehouseId() != null)
        {
            Warehouse warehouse = new Warehouse();
            warehouse.setWarehouseId(dto.getWarehouseId());
            receipt.setWarehouse(warehouse);
        }

        receipt.setStatus("PENDING");
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setReceivedAt(null);
        User users = new User();
        users.setUserId(userId);
        receipt.setUser(users);
        if (dto.getDeliveryNoteCode() != null) {
            receipt.setDeliveryNoteCode(dto.getDeliveryNoteCode());
        }


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

    @Override
    @Transactional
    public void approveInboundReceipt(Long receiptId, String adminNote) {
        InboundReceipt receipt = receiptRepo.findById(receiptId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu"));

        if (!"PENDING".equals(receipt.getStatus())) {
            throw new RuntimeException("Phiếu này đã được xử lý rồi!");
        }

        // Duyệt qua từng item trong phiếu để cộng kho
        for (InboundReceiptItem item : receipt.getItems()) {

            // Cập nhật số dư (Balance)
            var balance = balanceRepo.findByWarehouseIdAndLocationIdAndProductIdAndBatchId(
                    receipt.getWarehouse().getWarehouseId(),
                    item.getPutawayLocationId(),
                    item.getProduct().getProduct_id(),
                    item.getBatch() != null ? item.getBatch().getBatchId() : null
            ).orElse(new InventoryLocationBalance());

            // Nếu là dòng mới thì set thông tin cơ bản
            if (balance.getId() == null) {
                balance.setWarehouseId(receipt.getWarehouse().getWarehouseId());
                balance.setLocationId(item.getPutawayLocationId());
                balance.setProductId(item.getProduct().getProduct_id());
                balance.setBatchId(item.getBatch() != null ? item.getBatch().getBatchId() : null);
                balance.setQty(BigDecimal.ZERO);
                balance.setStatus("AVAILABLE");
            }

            // Cộng dồn thực tế
            balance.setQty(balance.getQty().add(item.getActualQty()));
            balance.setUpdateAt(LocalDateTime.now());
            balanceRepo.save(balance);

            // Ghi lịch sử kho (History)
            InventoryHistory history = new InventoryHistory();
            history.setTransactionType("INBOUND");
            history.setProductId(item.getProduct().getProduct_id());
            history.setQtyChange(item.getActualQty());
            history.setToLocationId(item.getPutawayLocationId());
            history.setWarehouseId(receipt.getWarehouse().getWarehouseId());
            history.setCreatedAt(LocalDateTime.now());
            historyRepo.save(history);
        }

        // Cập nhật trạng thái phiếu
        receipt.setStatus("COMPLETED");
        receipt.setReceivedAt(LocalDateTime.now());

        receiptRepo.save(receipt);

        if (receipt.getPurchaseOrders() != null) {
            poRepository.findById(receipt.getPurchaseOrders().getId()).ifPresent(po -> {
                po.setStatus("COMPLETED");
                poRepository.save(po);
            });
        }
    }

    @Override
    @Transactional
    public void rejectInboundReceipt(Long receiptId, String adminNote) {
        InboundReceipt receipt = receiptRepo.findById(receiptId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu"));

        if (!"PENDING".equals(receipt.getStatus())) {
            throw new RuntimeException("Chỉ có thể từ chối phiếu đang chờ duyệt!");
        }

        // Đổi trạng thái thành REJECTED (Từ chối)
        receipt.setStatus("REJECTED");
        receipt.setReceivedAt(LocalDateTime.now());

        if (receipt.getPurchaseOrders() != null) {
            for (InboundReceiptItem item : receipt.getItems()) {
                // Lấy số lượng thực tế đã nhập, chuyển thành số ÂM để trừ đi
                // Tùy thuộc vào hàm updateReceivedQty của bạn nhận kiểu Double hay BigDecimal
                // Ví dụ nếu nó nhận Double:
                double qtyToRollback = -item.getActualQty().doubleValue();

                poItemRepo.updateReceivedQty(
                        receipt.getPurchaseOrders().getId(),
                        item.getProduct().getProduct_id(),
                        qtyToRollback
                );
            }
        }
        receiptRepo.save(receipt);
    }

    @Override
    public List<InboundReceipt> getHistoryReceipts() {
        return receiptRepo.findByStatusInOrderByCreatedAtDesc(List.of("COMPLETED", "REJECTED"));
    }

}