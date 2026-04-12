package com.tttn.warehouseqr.modules.transfer.services.imp;

import com.tttn.warehouseqr.modules.auth.entity.User;
import com.tttn.warehouseqr.modules.auth.repository.UserRepository;
import com.tttn.warehouseqr.modules.inventory.service.InventoryServiceImpl;

import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseLocationRepository;
import com.tttn.warehouseqr.modules.transfer.dto.TransferItemDTO;
import com.tttn.warehouseqr.modules.transfer.dto.TransferRequestDTO;
import com.tttn.warehouseqr.modules.transfer.entity.TransferOrder;
import com.tttn.warehouseqr.modules.transfer.entity.TransferOrderItems;
import com.tttn.warehouseqr.modules.transfer.repository.TransferOrderItemReposiroty;
import com.tttn.warehouseqr.modules.transfer.repository.TransferOrderRepository;
import com.tttn.warehouseqr.modules.transfer.services.TransferOrderServices;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class TransferOrderServicesImp  implements TransferOrderServices {

    private final TransferOrderRepository transferOrderRepository;

    private final TransferOrderItemReposiroty itemReposiroty;

    private final InventoryServiceImpl inventoryService;

    private final ProductRepository productRepository;

    private final ProductBatchRepository  productBatchRepository;

    private final WarehouseLocationRepository  warehouseLocationRepository;

    private final UserRepository userRepository;

    public TransferOrderServicesImp(TransferOrderRepository transferOrderRepository, TransferOrderItemReposiroty itemReposiroty, InventoryServiceImpl inventoryService, ProductRepository productRepository, ProductBatchRepository productBatchRepository, WarehouseLocationRepository warehouseLocationRepository, UserRepository userRepository) {
        this.transferOrderRepository = transferOrderRepository;
        this.itemReposiroty = itemReposiroty;
        this.inventoryService = inventoryService;
        this.productRepository = productRepository;
        this.productBatchRepository = productBatchRepository;
        this.warehouseLocationRepository = warehouseLocationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void processTransfer(TransferRequestDTO request, Long userId) {

        // 1. Lưu thông tin Header (transfer_orders)

        User users = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        TransferOrder transfer = new TransferOrder();
        transfer.setFromWarehouseId(request.getFromWarehouseId());
        transfer.setToWarehouseId(request.getToWarehouseId());
        transfer.setOutboundReceiptId(request.getOutboundReceiptId());
        transfer.setCreator(users);

        transfer.setTransferDate(java.time.LocalDateTime.now());
        transfer.setStatus("COMPLETED"); // Hoặc trạng thái bạn quy định
        // Tự sinh mã phiếu điều chuyển (Ví dụ: TRF + Timestamp)
        transfer.setTransferCode("TRF-" + System.currentTimeMillis());
        transfer = transferOrderRepository.save(transfer);

        // 2. Lưu chi tiết (transfer_order_items) và cập nhật kho
        for (TransferItemDTO itemDto : request.getItems()) {
            TransferOrderItems item = new TransferOrderItems();
            item.setTransferOrder(transfer);
            item.setProductId(itemDto.getProductId());
            item.setBatchId(itemDto.getBatchId());
            item.setQty(itemDto.getActualQty());
            item.setFromLocationId(itemDto.getLocationId());

            if (itemDto.getTargetLocationId() == null) {
                throw new RuntimeException("Vị trí đích (Target Location) cho sản phẩm " + itemDto.getProductId() + " không được để trống");
            }

            item.setToLocationId(itemDto.getTargetLocationId());

            // FIX: Lấy kho đích từ item hoặc fallback về kho đích chung của đơn hàng
            Long finalToWarehouseId = (itemDto.getToWarehouseId() != null)
                    ? itemDto.getToWarehouseId()
                    : request.getToWarehouseId();

            if (finalToWarehouseId == null) {
                throw new RuntimeException("Kho đích không được xác định");
            }

            item.setToWarehouseId(finalToWarehouseId);

            itemReposiroty.save(item);

            // 3. Cập nhật bảng inventory_location_balances (ĐÃ THÊM PRODUCT ID)

            // Trừ nguồn: truyền đủ 5 tham số (Warehouse, Location, Product, Batch, Qty)
            inventoryService.reduceStock(
                    request.getFromWarehouseId(),
                    itemDto.getLocationId(),
                    itemDto.getProductId(),
                    itemDto.getBatchId(),
                    itemDto.getActualQty()
            );

            // Cộng đích: truyền đủ 5 tham số
            inventoryService.addStock(
                    finalToWarehouseId,
                    item.getToLocationId(),
                    itemDto.getProductId(),
                    itemDto.getBatchId(),
                    itemDto.getActualQty()
            );
        }
    }

    @Override
    public List<TransferItemDTO> parseTransferCsv(MultipartFile file) {
        List<TransferItemDTO> dtos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            // Xử lý BOM cho file UTF-8
            reader.mark(1);
            if (reader.read() != 0xFEFF) reader.reset();

            CSVParser parser = new CSVParser(reader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            for (CSVRecord record : parser) {
                String sku = record.get("SKU");
                String lotCode = record.get("Mã Lô Hàng");
                String qtyStr = record.get("Số Lượng");
                String fromLocationCode = record.get("Mã Vị Trí Nguồn");
                // String targetWhIdStr = record.get("ID Kho Đích"); // Nếu có trong CSV

                // 1. Tìm Sản phẩm
                Product product = productRepository.findBySku(sku);
                if (product == null) throw new RuntimeException("Không tìm thấy SKU: " + sku);

                // 2. Tìm Lô hàng
                var batch = productBatchRepository.findByLotCodeAndProductProduct_id(lotCode, product.getProduct_id())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy Lô " + lotCode + " cho sản phẩm " + sku));

                // 3. Tìm Vị trí nguồn (From Location)
                var fromLocation = warehouseLocationRepository.findByLocationCode(fromLocationCode)
                        .orElseThrow(() -> new RuntimeException("Vị trí nguồn không tồn tại: " + fromLocationCode));

                // 4. Tạo DTO khớp với logic Trạm quét
                TransferItemDTO dto = new TransferItemDTO();
                dto.setProductId(product.getProduct_id());
                dto.setProductName(product.getProductName()); // Thêm trường này nếu cần hiển thị UI
                dto.setLotCode(lotCode);
                dto.setBatchId(batch.getBatchId());
                dto.setActualQty(new BigDecimal(qtyStr));
                dto.setLocationId(fromLocation.getLocationId());
                dto.setLocationCode(fromLocationCode);

                // Mặc định nạp Kho đích từ giao diện, hoặc lấy từ CSV nếu có cột
                // dto.setToWarehouseId(Long.parseLong(targetWhIdStr));

                dtos.add(dto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file CSV điều chuyển: " + e.getMessage());
        }
        return dtos;
    }
}
