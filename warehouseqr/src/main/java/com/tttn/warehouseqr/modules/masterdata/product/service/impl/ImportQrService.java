package com.tttn.warehouseqr.modules.masterdata.product.service.impl;

import com.tttn.warehouseqr.modules.inventory.entity.InventoryHistory;
import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryHistoryRepository;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.masterdata.category.entity.ProductCategory;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch;
import com.tttn.warehouseqr.modules.masterdata.product.entity.QrCode;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.QrCodeResipotory;
import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;
import com.tttn.warehouseqr.modules.masterdata.unit.entity.Unit;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.WarehouseLocation;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseLocationRepository;
import com.tttn.warehouseqr.utils.QrCodeUtil;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ImportQrService {
    private final ProductRepository productRepository;
    private final ProductBatchRepository productBatchRepository;
    private final QrCodeResipotory qrCodeResipotory;

    private final InventoryHistoryRepository inventoryHistoryRepository;
    private final InventoryLocationBalanceRepository inventoryLocationBalanceRepository;
    private final WarehouseLocationRepository warehouseLocationRepository;

    public ImportQrService(ProductRepository productRepository,
                           ProductBatchRepository productBatchRepository,
                           QrCodeResipotory qrCodeResipotory,
                           InventoryHistoryRepository inventoryHistoryRepository,
                           InventoryLocationBalanceRepository inventoryLocationBalanceRepository,
                           WarehouseLocationRepository warehouseLocationRepository) {
        this.productRepository = productRepository;
        this.productBatchRepository = productBatchRepository;
        this.qrCodeResipotory = qrCodeResipotory;
        this.inventoryHistoryRepository = inventoryHistoryRepository;
        this.inventoryLocationBalanceRepository = inventoryLocationBalanceRepository;
        this.warehouseLocationRepository = warehouseLocationRepository;
    }

    @Transactional
    public void importCsvAndGenerateQr(MultipartFile file){
        try (BufferedReader fileRender = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileRender, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())){

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for(CSVRecord csvRecord : csvRecords){
                String sku = csvRecord.get("SKU");
                String productName = csvRecord.get("Tên Sản phẩm");
                String description = csvRecord.get("Mô Tả");
                String minStock = csvRecord.get("Tồn Kho Tối Thiểu");
                String categoryId = csvRecord.get("Mã Danh Mục");
                String unitId = csvRecord.get("Mã Đơn Vị");

                String batchCode = csvRecord.get("Mã Lô Hàng");
                String serialNum = csvRecord.get("Serial Number");
                String costPrice = csvRecord.get("Giá Nhập");
                String expiryDate = csvRecord.get("Ngày Hết Hạn");
                String  supplierId = csvRecord.get("Mã NCC");
                Long parseSupplierId = parseLong(supplierId, null);

                String qtyStr = csvRecord.isSet("Số Lượng") ? csvRecord.get("Số Lượng") : "0";
                String locationCodeStr = csvRecord.isSet("Mã Vị Trí") ? csvRecord.get("Mã Vị Trí") : "";


                Product product = productRepository.findBySku(sku);
                if(product == null){
                    product = new Product();
                    product.setSku(sku);
                    product.setProductName(productName);
                    product.setDescription(description.isEmpty() ? null : description);
                    product.setMinStock(parseBigDecimal(minStock));

                    ProductCategory category = new ProductCategory();
                    category.setCategoryId(parseLong(categoryId, 1L));
                    product.setCategory(category);

                    Unit unit = new Unit();
                    unit.setUnitId(parseLong(unitId, 1L));
                    product.setUnit(unit);

                    productRepository.save(product);
                }

                ProductBatch batch = new ProductBatch();
                batch.setLotCode(batchCode);
                batch.setSerialNumber(serialNum.isEmpty() ? null : serialNum);
                batch.setCostPrice(parseBigDecimal(costPrice));
                if(parseSupplierId != null){
                    Supplier supplier = new Supplier();
                    supplier.setSupplierId(parseSupplierId);
                    batch.setSupplier(supplier);
                }
                batch.setProduct(product);

                if (!expiryDate.isEmpty()){
                    batch.setExpiryDate(LocalDate.parse(expiryDate));
                }
                batch = productBatchRepository.save(batch);

                String qrContent = sku + "|" + batchCode;
                String base64Image = QrCodeUtil.GenerateQRCodeBase64(qrContent,300,300);

                QrCode qrCode = new QrCode();
                qrCode.setQrContent(qrContent);
                qrCode.setImgPath(base64Image);
                qrCode.setReferenceType("BATCH");
                qrCode.setReferenceId(batch.getBatchId());
                qrCode.setPrinted(false);

                qrCodeResipotory.save(qrCode);

                double importQty = 0;
                try {
                    importQty = Double.parseDouble(qtyStr);
                }
                catch (NumberFormatException e){
                    importQty = 0;
                }

                if(importQty > 0 && !locationCodeStr.isEmpty()){
                    WarehouseLocation location = warehouseLocationRepository.findByLocationCode(locationCodeStr).orElseGet(
                            () -> {
                                WarehouseLocation newLocation = new WarehouseLocation();
                                newLocation.setLocationCode(locationCodeStr);
                                com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse defaultWarehouse = new com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse();
                                defaultWarehouse.setWarehouseId(1L);
                                newLocation.setWarehouses(defaultWarehouse);
                                return warehouseLocationRepository.save(newLocation);
                            }
                    );

                    Optional<InventoryLocationBalance> balanceOpt = inventoryLocationBalanceRepository.findByBatchIdAndLocationId(batch.getBatchId(), location.getLocationId());
                    InventoryLocationBalance balance;

                    if (balanceOpt.isPresent()){
                        balance = balanceOpt.get();
                        BigDecimal currentQty = balance.getQty() != null ? balance.getQty() : BigDecimal.ZERO;
                        balance.setQty(currentQty.add(BigDecimal.valueOf(importQty)));
                    }
                    else {
                        balance = new InventoryLocationBalance();
                        balance.setBatchId(batch.getBatchId());
                        balance.setLocationId(location.getLocationId());
                        balance.setProductId(product.getProduct_id());
                        balance.setWarehouseId(location.getWarehouses().getWarehouseId());
                        balance.setQty(BigDecimal.valueOf(importQty));
                    }
                    balance.setUpdateAt(LocalDateTime.now());
                    inventoryLocationBalanceRepository.save(balance);

                    InventoryHistory history = new InventoryHistory();
                    history.setTransactionType("INITIAL_IMPORT");
                    history.setProductId(product.getProduct_id());
                    history.setBatchId(batch.getBatchId());
                    history.setToLocationId(location.getLocationId());
                    history.setQtyChange(BigDecimal.valueOf(importQty));
                    history.setWarehouseId(location.getWarehouses().getWarehouseId());
                    history.setCreatedAt(LocalDateTime.now());
                    inventoryHistoryRepository.save(history);
                }
            }
        }
        catch (Exception e){
            throw new RuntimeException("Lỗi khi đọc file CSV: " + e.getMessage());
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return BigDecimal.ZERO;
        try { return new BigDecimal(value.trim()); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    private Long parseLong(String value, Long defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try { return Long.parseLong(value.trim()); } catch (Exception e) { return defaultValue; }
    }

    @Transactional
    public void generateManualQr(List<Long> batchIds) {
        for (Long batchId : batchIds) {
            QrCode existingQr = qrCodeResipotory.findByReferenceIdAndReferenceType(batchId, "BATCH");

            if (existingQr == null) {
                ProductBatch batch = productBatchRepository.findById(batchId).orElse(null);
                if (batch != null) {

                    String qrContent = batch.getProduct().getSku() + "|" + batch.getLotCode();

                    String base64Image = QrCodeUtil.GenerateQRCodeBase64(qrContent, 300, 300);

                    QrCode qrCode = new QrCode();
                    qrCode.setQrContent(qrContent);
                    qrCode.setImgPath(base64Image);
                    qrCode.setReferenceType("BATCH");
                    qrCode.setReferenceId(batchId);
                    qrCode.setPrinted(false);

                    qrCodeResipotory.save(qrCode);
                }
            }
        }
    }
}
