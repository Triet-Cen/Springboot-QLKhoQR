package com.tttn.warehouseqr.modules.transfer.services.imp;

import com.tttn.warehouseqr.modules.auth.entity.User;
import com.tttn.warehouseqr.modules.auth.repository.UserRepository;
import com.tttn.warehouseqr.modules.inventory.service.InventoryServiceImpl;

import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseLocationRepository;
import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceipt;
import com.tttn.warehouseqr.modules.outbound.repository.OutboundReceiptRepository;
import com.tttn.warehouseqr.modules.transfer.dto.TransferItemDTO;
import com.tttn.warehouseqr.modules.transfer.dto.TransferRequestDTO;
import com.tttn.warehouseqr.modules.transfer.entity.TransferOrder;
import com.tttn.warehouseqr.modules.transfer.entity.TransferOrderItems;
import com.tttn.warehouseqr.modules.transfer.repository.TransferOrderItemRepository;
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

    private final TransferOrderItemRepository itemReposiroty;

    private final InventoryServiceImpl inventoryService;

    private final ProductRepository productRepository;

    private final ProductBatchRepository  productBatchRepository;

    private final WarehouseLocationRepository  warehouseLocationRepository;

    private final UserRepository userRepository;

    private final OutboundReceiptRepository outboundReceiptRepository;

    public TransferOrderServicesImp(TransferOrderRepository transferOrderRepository, TransferOrderItemRepository itemReposiroty, InventoryServiceImpl inventoryService, ProductRepository productRepository, ProductBatchRepository productBatchRepository, WarehouseLocationRepository warehouseLocationRepository, UserRepository userRepository, OutboundReceiptRepository outboundReceiptRepository) {
        this.transferOrderRepository = transferOrderRepository;
        this.itemReposiroty = itemReposiroty;
        this.inventoryService = inventoryService;
        this.productRepository = productRepository;
        this.productBatchRepository = productBatchRepository;
        this.warehouseLocationRepository = warehouseLocationRepository;
        this.userRepository = userRepository;
        this.outboundReceiptRepository = outboundReceiptRepository;
    }

    @Override
    @Transactional
    public void processTransfer(TransferRequestDTO request, Long userId) {

        User users = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Xác định Kịch bản dựa vào Kho Xuất và Kho Đích
        boolean isInternalTransfer = request.getFromWarehouseId().equals(request.getToWarehouseId());

        if (isInternalTransfer) {
            // =====================================================================
            // KỊCH BẢN 1: ĐIỀU CHUYỂN NỘI BỘ (CHUYỂN KỆ TRONG CÙNG 1 KHO)
            // - Xử lý ngay lập tức, KHÔNG CẦN DUYỆT.
            // - KHÔNG tạo Phiếu Xuất Kho (OutboundReceipt) vì hàng chưa ra khỏi nhà.
            // =====================================================================

            // 1. Tạo Header Điều chuyển nội bộ
            TransferOrder transfer = new TransferOrder();
            transfer.setFromWarehouseId(request.getFromWarehouseId());
            transfer.setToWarehouseId(request.getToWarehouseId());
            transfer.setCreator(users);
            transfer.setTransferDate(java.time.LocalDateTime.now());
            transfer.setStatus("COMPLETED"); // Hoàn thành ngay lập tức
            transfer.setTransferCode("MV-" + System.currentTimeMillis()); // MV = Move (Dời vị trí)
            transfer = transferOrderRepository.save(transfer);

            for (TransferItemDTO itemDto : request.getItems()) {
                // Kiểm tra an toàn: Dời kệ thì kệ xuất và kệ đích không được trùng nhau
                if (itemDto.getLocationId().equals(itemDto.getTargetLocationId())) {
                    throw new RuntimeException("Chuyển nội bộ không thể chuyển vào cùng một kệ!");
                }

                // Lưu chi tiết
                TransferOrderItems transferItem = new TransferOrderItems();
                transferItem.setTransferOrder(transfer);
                transferItem.setProductId(itemDto.getProductId());
                transferItem.setBatchId(itemDto.getBatchId());
                transferItem.setQty(itemDto.getActualQty());
                transferItem.setFromLocationId(itemDto.getLocationId());
                transferItem.setToLocationId(itemDto.getTargetLocationId());
                transferItem.setToWarehouseId(request.getToWarehouseId());
                itemReposiroty.save(transferItem);

                // TRỪ KHO (Kệ cũ)
                inventoryService.reduceStock(
                        request.getFromWarehouseId(),
                        itemDto.getLocationId(),
                        itemDto.getProductId(),
                        itemDto.getBatchId(),
                        itemDto.getActualQty()
                );

                // CỘNG KHO (Kệ mới)
                inventoryService.addStock(
                        request.getToWarehouseId(),
                        itemDto.getTargetLocationId(),
                        itemDto.getProductId(),
                        itemDto.getBatchId(),
                        itemDto.getActualQty()
                );
            }

        } else {
            // =====================================================================
            // KỊCH BẢN 2: ĐIỀU CHUYỂN LIÊN KHO (TỪ KHO A SANG KHO B)
            // - Hàng phải lên xe tải đi đường -> BẮT BUỘC PHẢI CHỜ DUYỆT.
            // - Tồn kho KHÔNG BỊ TRỪ lúc này.
            // =====================================================================

            // 1. Tạo Header Điều chuyển liên kho
            TransferOrder transfer = new TransferOrder();
            transfer.setFromWarehouseId(request.getFromWarehouseId());
            transfer.setToWarehouseId(request.getToWarehouseId());
            transfer.setCreator(users);
            transfer.setTransferDate(java.time.LocalDateTime.now());
            transfer.setStatus("PENDING_APPROVAL"); // Treo trạng thái chờ duyệt!
            transfer.setTransferCode("TRF-" + System.currentTimeMillis()); // TRF = Transfer
            transfer = transferOrderRepository.save(transfer);

            for (TransferItemDTO itemDto : request.getItems()) {
                if (itemDto.getTargetLocationId() == null) {
                    throw new RuntimeException("Vị trí đích cho sản phẩm " + itemDto.getProductId() + " bị trống!");
                }

                // Lưu chi tiết
                TransferOrderItems transferItem = new TransferOrderItems();
                transferItem.setTransferOrder(transfer);
                transferItem.setProductId(itemDto.getProductId());
                transferItem.setBatchId(itemDto.getBatchId());
                transferItem.setQty(itemDto.getActualQty());
                transferItem.setFromLocationId(itemDto.getLocationId());
                transferItem.setToLocationId(itemDto.getTargetLocationId());

                Long finalToWarehouseId = (itemDto.getToWarehouseId() != null) ? itemDto.getToWarehouseId() : request.getToWarehouseId();
                transferItem.setToWarehouseId(finalToWarehouseId);

                itemReposiroty.save(transferItem);

                // QUAN TRỌNG: Không gọi inventoryService ở đây!
                // Việc trừ/cộng kho và tạo OutboundReceipt sẽ được thực hiện
                // ở một hàm khác (ví dụ: approveTransferOrder) khi Quản lý bấm nút Duyệt.
            }
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

    @Override
    public void approveTransferOrder(Long transferOrderId, Long approverId) {
        // 1. Tìm đơn điều chuyển
        TransferOrder transfer = transferOrderRepository.findById(transferOrderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lệnh điều chuyển!"));

        // 2. Kiểm tra trạng thái: Phải là đơn đang chờ duyệt mới được thao tác
        if (!"PENDING_APPROVAL".equals(transfer.getStatus())) {
            throw new RuntimeException("Lỗi: Chỉ có thể duyệt lệnh đang ở trạng thái Chờ phê duyệt!");
        }

        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Người duyệt không tồn tại"));

        // =====================================================================
        // 3. TẠO PHIẾU XUẤT KHO (MINH CHỨNG HÀNG LÊN XE)
        // =====================================================================
        OutboundReceipt outbound = new OutboundReceipt();
        outbound.setOutboundReceiptCode("PX-TRF-" + System.currentTimeMillis());
        outbound.setWarehouseId(transfer.getFromWarehouseId());
        outbound.setStatus("COMPLETED");
        outbound.setCreatedBy(approver.getUserId());
        outbound.setCreatedAt(java.time.LocalDateTime.now());
        outbound = outboundReceiptRepository.save(outbound);

        // 4. Liên kết phiếu xuất vào đơn chuyển kho & Cập nhật trạng thái
        transfer.setOutboundReceiptId(outbound.getId());
        transfer.setStatus("COMPLETED"); // Đã duyệt và hoàn tất chuyển
        transfer = transferOrderRepository.save(transfer);

        // =====================================================================
        // 5. LẤY DANH SÁCH HÀNG HÓA VÀ BẮT ĐẦU TRỪ/CỘNG KHO
        // =====================================================================
        List<TransferOrderItems> items = itemReposiroty.findByTransferOrder_Id(transfer.getId());

        for (TransferOrderItems item : items) {
            // TRỪ KHO NGUỒN (Lấy hàng đi)
            inventoryService.reduceStock(
                    transfer.getFromWarehouseId(),
                    item.getFromLocationId(),
                    item.getProductId(),
                    item.getBatchId(),
                    item.getQty()
            );

            // CỘNG KHO ĐÍCH (Nhập hàng tới)
            inventoryService.addStock(
                    item.getToWarehouseId(),
                    item.getToLocationId(),
                    item.getProductId(),
                    item.getBatchId(),
                    item.getQty()
            );
        }
    }

    @Override
    public void rejectTransferOrder(Long transferOrderId, String rejectReason, Long approverId) {
        TransferOrder transfer = transferOrderRepository.findById(transferOrderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lệnh điều chuyển!"));

        if (!"PENDING_APPROVAL".equals(transfer.getStatus())) {
            throw new RuntimeException("Lỗi: Chỉ có thể từ chối lệnh đang chờ phê duyệt!");
        }

        // Hủy đơn, giữ nguyên tồn kho (không gọi inventoryService)
        transfer.setStatus("REJECTED");
        // Nếu DB của bạn có cột ghi chú/lý do từ chối thì lưu vào đây
        // transfer.setNote(rejectReason);

        transferOrderRepository.save(transfer);
    }
}
