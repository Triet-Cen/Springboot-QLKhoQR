package com.tttn.warehouseqr.modules.Location.service.Impl;

import com.tttn.warehouseqr.modules.Location.dto.LocationInventoryScanDTO;
import com.tttn.warehouseqr.modules.Location.entity.StorageLocation;
import com.tttn.warehouseqr.modules.Location.repository.StorageLocationRepository;
import com.tttn.warehouseqr.modules.Location.service.StorageLocationService;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductScanDTO;
import com.tttn.warehouseqr.modules.masterdata.product.entity.QrCode;
import com.tttn.warehouseqr.modules.masterdata.product.repository.QrCodeResipotory;
import com.tttn.warehouseqr.utils.QrCodeUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StorageLocationServiceImpl implements StorageLocationService {

    private final StorageLocationRepository storageLocationRepository;
    private final QrCodeResipotory qrCodeResipotory;

    public StorageLocationServiceImpl(StorageLocationRepository storageLocationRepository,
                                      QrCodeResipotory qrCodeResipotory) {
        this.storageLocationRepository = storageLocationRepository;
        this.qrCodeResipotory = qrCodeResipotory;
    }

    @Override
    public List<StorageLocation> findAll() {
        List<StorageLocation> locations = storageLocationRepository.findAll();
        locations.forEach(this::syncUsageAndStatusFromInventory);
        return locations;
    }

    @Override
    public List<StorageLocation> search(String keyword, Long warehouseId, String status) {
        List<StorageLocation> locations = storageLocationRepository.findAll();

        locations.forEach(this::syncUsageAndStatusFromInventory);

        return locations.stream()
                .filter(loc -> warehouseId == null || warehouseId.equals(loc.getWarehouseId()))
                .filter(loc -> status == null || status.isBlank()
                        || (loc.getStatus() != null && loc.getStatus().equalsIgnoreCase(status)))
                .filter(loc -> {
                    if (keyword == null || keyword.isBlank()) return true;

                    String text = (
                            safe(loc.getLocationCode()) + " " +
                                    safe(loc.getAisleCode()) + " " +
                                    safe(loc.getRackCode()) + " " +
                                    safe(loc.getBinCode())
                    ).toLowerCase();

                    return text.contains(keyword.toLowerCase());
                })
                .collect(Collectors.toList());
    }

    @Override
    public StorageLocation findById(Long id) {
        StorageLocation location = storageLocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vị trí kho với id = " + id));

        syncUsageAndStatusFromInventory(location);
        return location;
    }

    @Override
    public StorageLocation save(StorageLocation location) {
        if (location.getWarehouseId() == null) {
            throw new RuntimeException("Vui lòng chọn kho.");
        }

        String code = location.getLocationCode() != null ? location.getLocationCode().trim() : "";
        if (code.isBlank()) {
            throw new RuntimeException("Mã vị trí không được để trống.");
        }

        if (storageLocationRepository.existsByLocationCode(code)) {
            throw new RuntimeException("Mã vị trí đã tồn tại: " + code);
        }

        location.setLocationCode(code);
        location.setQrCodeId(null); // luôn để hệ thống tự sinh

        if (location.getCapacity() == null) {
            location.setCapacity(0);
        }

        location.setUsedCapacity(0);

        if (location.getStatus() == null || location.getStatus().isBlank()) {
            location.setStatus("EMPTY");
        } else if (!"INACTIVE".equalsIgnoreCase(location.getStatus())) {
            location.setStatus("EMPTY");
        }

        StorageLocation saved = storageLocationRepository.save(location);
        generateOrUpdateLocationQr(saved);
        return saved;
    }

    @Override
    public StorageLocation update(Long id, StorageLocation location) {
        StorageLocation oldLocation = storageLocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vị trí kho với id = " + id));

        if (location.getWarehouseId() == null) {
            throw new RuntimeException("Vui lòng chọn kho.");
        }

        String code = location.getLocationCode() != null ? location.getLocationCode().trim() : "";
        if (code.isBlank()) {
            throw new RuntimeException("Mã vị trí không được để trống.");
        }

        if (storageLocationRepository.existsByLocationCodeAndLocationIdNot(code, id)) {
            throw new RuntimeException("Mã vị trí đã tồn tại: " + code);
        }

        oldLocation.setWarehouseId(location.getWarehouseId());
        oldLocation.setZoneId(location.getZoneId());
        oldLocation.setLocationCode(code);
        oldLocation.setAisleCode(location.getAisleCode());
        oldLocation.setRackCode(location.getRackCode());
        oldLocation.setBinCode(location.getBinCode());
        oldLocation.setCapacity(location.getCapacity());
        oldLocation.setDescription(location.getDescription());

        BigDecimal usedQty = storageLocationRepository.getUsedQtyByLocationId(id);
        oldLocation.setUsedCapacity(usedQty != null ? usedQty.intValue() : 0);

        if ("INACTIVE".equalsIgnoreCase(location.getStatus())) {
            oldLocation.setStatus("INACTIVE");
        } else {
            recalculateOperationalStatus(oldLocation);
        }

        StorageLocation saved = storageLocationRepository.save(oldLocation);
        generateOrUpdateLocationQr(saved);
        return saved;
    }

    @Override
    public List<ProductScanDTO> traceProductLocationsByQr(String qrContent) {
        if (qrContent == null || qrContent.isBlank()) {
            throw new RuntimeException("QR content không được để trống.");
        }

        QrCode qrCode = qrCodeResipotory.findByQrContent(qrContent.trim())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy QR trong hệ thống."));

        if (!"BATCH".equalsIgnoreCase(qrCode.getReferenceType())) {
            throw new RuntimeException("QR này không phải QR của lô hàng sản phẩm.");
        }

        Long batchId = qrCode.getReferenceId();

        List<StorageLocationRepository.ProductLocationView> rows =
                storageLocationRepository.findProductLocationsByBatchId(batchId);

        if (rows.isEmpty()) {
            throw new RuntimeException("Không tìm thấy vị trí đang chứa sản phẩm cho QR này.");
        }

        return rows.stream().map(r -> {
            ProductScanDTO dto = new ProductScanDTO();
            dto.setProductId(r.getProductId());
            dto.setProductName(r.getProductName());
            dto.setBatchId(r.getBatchId());
            dto.setLotCode(r.getLotCode());
            dto.setSku(r.getSku());
            dto.setActualQty(r.getQty() != null ? r.getQty().doubleValue() : 0.0);
            dto.setLocationId(r.getLocationId());
            dto.setLocationCode(r.getLocationCode());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<LocationInventoryScanDTO> traceInventoryByLocationQr(String qrContent) {
        if (qrContent == null || qrContent.isBlank()) {
            throw new RuntimeException("QR vị trí không được để trống.");
        }

        List<StorageLocationRepository.LocationInventoryView> rows =
                storageLocationRepository.findInventoryByLocationQr(qrContent.trim());

        if (rows.isEmpty()) {
            throw new RuntimeException("Không tìm thấy vị trí kho từ mã QR này.");
        }

        StorageLocationRepository.LocationInventoryView first = rows.get(0);

        if (first.getLocationId() == null) {
            throw new RuntimeException("Không tìm thấy vị trí kho từ mã QR này.");
        }

        if (first.getProductId() == null) {
            throw new RuntimeException("Vị trí " + first.getLocationCode() + " hiện chưa có sản phẩm nào.");
        }

        return rows.stream().map(r -> {
            LocationInventoryScanDTO dto = new LocationInventoryScanDTO();
            dto.setLocationId(r.getLocationId());
            dto.setLocationCode(r.getLocationCode());
            dto.setAisleCode(r.getAisleCode());
            dto.setRackCode(r.getRackCode());
            dto.setBinCode(r.getBinCode());
            dto.setZoneName(r.getZoneName());
            dto.setProductId(r.getProductId());
            dto.setProductName(r.getProductName());
            dto.setSku(r.getSku());
            dto.setBatchId(r.getBatchId());
            dto.setLotCode(r.getLotCode());
            dto.setQty(r.getQty() != null ? r.getQty().doubleValue() : 0.0);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getLocationQrInfo(Long locationId) {
        StorageLocation location = storageLocationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vị trí kho."));

        QrCode qrCode = qrCodeResipotory.findByReferenceIdAndReferenceType(locationId, "LOCATION");
        if (qrCode == null) {
            qrCode = generateOrUpdateLocationQr(location);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("locationId", location.getLocationId());
        result.put("locationCode", location.getLocationCode());
        result.put("qrCodeId", qrCode.getQrCodeId());
        result.put("qrContent", qrCode.getQrContent());
        result.put("imgBase64", qrCode.getImgPath());
        return result;
    }

    private QrCode generateOrUpdateLocationQr(StorageLocation location) {
        if (location.getLocationId() == null) {
            throw new RuntimeException("Vị trí kho chưa có ID nên chưa thể sinh QR.");
        }

        String qrContent = location.getLocationCode();
        String base64Image = QrCodeUtil.GenerateQRCodeBase64(qrContent, 300, 300);

        QrCode qrCode = qrCodeResipotory.findByReferenceIdAndReferenceType(location.getLocationId(), "LOCATION");

        if (qrCode == null) {
            qrCode = new QrCode();
            qrCode.setReferenceType("LOCATION");
            qrCode.setReferenceId(location.getLocationId());
            qrCode.setPrinted(false);
        }

        qrCode.setQrContent(qrContent);
        qrCode.setImgPath(base64Image);

        qrCode = qrCodeResipotory.save(qrCode);

        if (location.getQrCodeId() == null || !location.getQrCodeId().equals(qrCode.getQrCodeId())) {
            location.setQrCodeId(qrCode.getQrCodeId());
            storageLocationRepository.save(location);
        }

        return qrCode;
    }

    private void syncUsageAndStatusFromInventory(StorageLocation loc) {
        if (loc.getLocationId() == null) {
            loc.setUsedCapacity(0);
            if (!"INACTIVE".equalsIgnoreCase(loc.getStatus())) {
                loc.setStatus("EMPTY");
            }
            return;
        }

        BigDecimal usedQty = storageLocationRepository.getUsedQtyByLocationId(loc.getLocationId());
        loc.setUsedCapacity(usedQty != null ? usedQty.intValue() : 0);

        if (!"INACTIVE".equalsIgnoreCase(loc.getStatus())) {
            recalculateOperationalStatus(loc);
        }
    }

    private void recalculateOperationalStatus(StorageLocation loc) {
        int capacity = loc.getCapacity() != null ? loc.getCapacity() : 0;
        int used = loc.getUsedCapacity() != null ? loc.getUsedCapacity() : 0;

        if (used <= 0) {
            loc.setStatus("EMPTY");
        } else if (capacity > 0 && used >= capacity) {
            loc.setStatus("FULL");
        } else {
            loc.setStatus("ACTIVE");
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}