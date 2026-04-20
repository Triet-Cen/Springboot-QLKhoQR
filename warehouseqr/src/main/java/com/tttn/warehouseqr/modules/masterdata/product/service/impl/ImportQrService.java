package com.tttn.warehouseqr.modules.masterdata.product.service.impl;

import com.tttn.warehouseqr.modules.auth.entity.User;
import com.tttn.warehouseqr.modules.auth.repository.UserRepository;
import com.tttn.warehouseqr.modules.inventory.entity.InventoryHistory;
import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryHistoryRepository;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.masterdata.category.entity.ProductCategory;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch;
import com.tttn.warehouseqr.modules.masterdata.product.entity.QrCode;
import com.tttn.warehouseqr.modules.masterdata.product.entity.TempImportData;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.QrCodeResipotory;
import com.tttn.warehouseqr.modules.masterdata.product.repository.TempImportDataRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ImportQrService {
    private final ProductRepository productRepository;
    private final ProductBatchRepository productBatchRepository;
    private final QrCodeResipotory qrCodeResipotory;
    private final TempImportDataRepository tempImportDataRepository;

    private final InventoryHistoryRepository inventoryHistoryRepository;
    private final InventoryLocationBalanceRepository inventoryLocationBalanceRepository;
    private final WarehouseLocationRepository warehouseLocationRepository;
    private final UserRepository userRepository;

    public ImportQrService(ProductRepository productRepository,
                           ProductBatchRepository productBatchRepository,
                           QrCodeResipotory qrCodeResipotory,
                           InventoryHistoryRepository inventoryHistoryRepository,
                           InventoryLocationBalanceRepository inventoryLocationBalanceRepository,
                           WarehouseLocationRepository warehouseLocationRepository,
                           TempImportDataRepository tempImportDataRepository,
                           UserRepository userRepository) {
        this.productRepository = productRepository;
        this.productBatchRepository = productBatchRepository;
        this.qrCodeResipotory = qrCodeResipotory;
        this.inventoryHistoryRepository = inventoryHistoryRepository;
        this.inventoryLocationBalanceRepository = inventoryLocationBalanceRepository;
        this.warehouseLocationRepository = warehouseLocationRepository;
        this.tempImportDataRepository = tempImportDataRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String importCsvToTemp(MultipartFile file){
        try (BufferedReader fileRender = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))){
            String headerLine = fileRender.readLine();
            if (headerLine == null){
                throw new RuntimeException("File CSV trống!");
            }
            if(headerLine.startsWith("\uFEFF")){
                headerLine = headerLine.substring(1);
            }
            String[] cleanHeader = Arrays.stream(headerLine.split(",")).map(String::trim).toArray(String[]::new);

            String currentSessionId = "SESSION_" + System.currentTimeMillis();

            try(CSVParser csvParser = new CSVParser(fileRender, CSVFormat.DEFAULT.withHeader(cleanHeader).withIgnoreHeaderCase().withTrim())){
                tempImportDataRepository.deleteAll();

                Iterable<CSVRecord> csvRecords = csvParser.getRecords();

                for (CSVRecord csvRecord : csvRecords){
                    TempImportData tempData = new TempImportData();
                    tempData.setSku(csvRecord.get("SKU"));
                    tempData.setProductName(csvRecord.get("Tên Sản Phẩm"));
                    tempData.setDescription(csvRecord.get("Mô Tả"));
                    tempData.setMinStock(csvRecord.get("Tồn Kho Tối Thiểu"));
                    tempData.setCategoryId(csvRecord.get("Mã Danh Mục"));
                    tempData.setUnitId(csvRecord.get("Mã Đơn Vị"));
                    tempData.setBatchCode(csvRecord.get("Mã Lô Hàng"));
                    tempData.setSerialNum(csvRecord.get("Serial Number"));
                    tempData.setCostPrice(csvRecord.get("Giá Nhập"));
                    tempData.setExpiryDate(csvRecord.get("Ngày Hết Hạn"));
                    tempData.setSupplierId(csvRecord.get("Mã NCC"));

                    tempData.setQuantity(csvRecord.isSet("Số Lượng") ? csvRecord.get("Số Lượng") : "0");
                    tempData.setLocationCode(csvRecord.isSet("Mã Vị Trí") ? csvRecord.get("Mã Vị Trí") : "");
                    tempData.setWarehouseId(csvRecord.isSet("Mã Kho") ? csvRecord.get("Mã Kho") : "1");


                    tempData.setImportSessionId(currentSessionId);
                    tempData.setValidationStatus("PENDING");
                    tempData.setValidationMessage("Đang chờ kiểm tra dữ liệu...");

                    tempImportDataRepository.save(tempData);
                }
            }
            return currentSessionId;
        }
        catch (Exception e){
                throw new RuntimeException("Lỗi khi đọc file CSV: " + e.getMessage());
        }
    }

        @Transactional
        public void ValidateTempData(String sessionId){
            List<TempImportData> temps = tempImportDataRepository.findByImportSessionId(sessionId);

            for (TempImportData temp : temps){

                if(isAnyFieldEmpty(temp)){
                    temp.setValidationStatus("INVALID");
                    temp.setValidationMessage("Lỗi: Dữ liệu bị thiếu! Phải điền đủ tất cả dữ liệu không được để trống.");
                    continue;
                }

                try {
                    double checkQty = Double.parseDouble(temp.getQuantity());
                    if (checkQty <= 0) {
                        temp.setValidationStatus("INVALID");
                        temp.setValidationMessage("Lỗi: Số lượng nhập kho phải lớn hơn 0!");
                    }
                } catch (NumberFormatException e) {
                    temp.setValidationStatus("INVALID");
                    temp.setValidationMessage("Lỗi: Số lượng phải là định dạng số hợp lệ!");
                }

                Product existingProduct = productRepository.findBySku(temp.getSku());

                if(existingProduct != null){
                    Optional<ProductBatch> existingBatch = productBatchRepository.findFirstByProduct(existingProduct);

                    if (existingBatch.isPresent()){
                        temp.setValidationStatus("UPDATE");
                        temp.setValidationMessage("Sản phẩm đã có. Sẽ cộng dồn vào lô hàng trong kho!");
                    }
                    else {
                        temp.setValidationStatus("NEW_BATCH");
                        temp.setValidationMessage("Sản phẩm đã có nhưng chưa có lô. Tạo mới lô tự động cho sản phẩm!");
                    }
                }
                else {
                    temp.setValidationStatus("INSERT");
                    temp.setValidationMessage("Sản phẩm mới. Sẽ tạo lô sản phẩm và lô hàng mới!");
                }

                tempImportDataRepository.save(temp);
            }
        }

            @Transactional
            public String confirmImport(String sessionId, Long currentUserId){
                List<TempImportData> temps = tempImportDataRepository.findByImportSessionId(sessionId);

                int successCount = 0;
                int errorCount = 0;

                for(TempImportData temp: temps){
                    if(temp.getValidationStatus().equals("INVALID")){
                        errorCount++;
                        continue;
                    }
                    if (temp.getValidationStatus().equals("UPDATE")){
                        Product p = productRepository.findBySku(temp.getSku());
                        Optional<ProductBatch> b = productBatchRepository.findFirstByProduct(p);
                        if(b.isPresent()){
                            updateInventoryQuantity(b.get(),temp,currentUserId);
                            successCount++;
                        }

                    }
                    else if (temp.getValidationStatus().equals("INSERT") || temp.getValidationStatus().equals("NEW_BATCH")){
                            createNewProductAndBatch(temp, currentUserId);
                            successCount++;
                    }
                }
                tempImportDataRepository.deleteAll(temps);

                if (successCount == 0 && errorCount > 0) {
                    throw new RuntimeException("Tất cả dữ liệu đều bị lỗi (" + errorCount + " dòng). Không có sản phẩm nào được nhập!");
                }

                return "Đã nhập thành công " + successCount + " sản phẩm. Bỏ qua " + errorCount + " dòng dữ liệu lỗi.";
            }

//    @Transactional
//    public void importCsvAndGenerateQr(MultipartFile file) {
//        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(),
//                StandardCharsets.UTF_8))) {
//
//            String headerLine = fileReader.readLine();
//            if (headerLine == null) {
//                throw new RuntimeException("File CSV trống!");
//            }
//
//            if (headerLine.startsWith("\uFEFF")) {
//                headerLine = headerLine.substring(1);
//            }
//
//            String[] cleanHeaders = Arrays.stream(headerLine.split(","))
//                    .map(String::trim)
//                    .toArray(String[]::new);
//
//            try (CSVParser csvParser = new CSVParser(fileReader,
//                    CSVFormat.DEFAULT.withHeader(cleanHeaders).withIgnoreHeaderCase().withTrim())) {
//
//                Iterable<CSVRecord> csvRecords = csvParser.getRecords();
//
//                for (CSVRecord csvRecord : csvRecords) {
//                    String sku = csvRecord.get("SKU");
//                    String productName = csvRecord.get("Tên Sản Phẩm"); // Chữ 'P' viết hoa theo chuẩn file CSV
//                    String description = csvRecord.get("Mô Tả");
//                    String minStock = csvRecord.get("Tồn Kho Tối Thiểu");
//                    String categoryId = csvRecord.get("Mã Danh Mục");
//                    String unitId = csvRecord.get("Mã Đơn Vị");
//
//                    String batchCode = csvRecord.get("Mã Lô Hàng");
//                    String serialNum = csvRecord.get("Serial Number");
//                    String costPrice = csvRecord.get("Giá Nhập");
//                    String expiryDate = csvRecord.get("Ngày Hết Hạn");
//                    String supplierId = csvRecord.get("Mã NCC");
//                    Long parseSupplierId = parseLong(supplierId, null);
//
//                    String qtyStr = csvRecord.isSet("Số Lượng") ? csvRecord.get("Số Lượng") : "0";
//                    String locationCodeStr = csvRecord.isSet("Mã Vị Trí") ? csvRecord.get("Mã Vị Trí") : "";
//                    String warehouseIdStr = csvRecord.isSet("Mã Kho") ? csvRecord.get("Mã Kho") : "1";
//
//                    Long warehouseId = parseLong(warehouseIdStr, 1L);
//
//                    Product product = productRepository.findBySku(sku);
//                    if (product == null) {
//                        product = new Product();
//                        product.setSku(sku);
//                        product.setProductName(productName);
//                        product.setDescription(description.isEmpty() ? null : description);
//                        product.setMinStock(parseBigDecimal(minStock));
//
//                        ProductCategory category = new ProductCategory();
//                        category.setCategoryId(parseLong(categoryId, 1L));
//                        product.setCategory(category);
//
//                        Unit unit = new Unit();
//                        unit.setUnitId(parseLong(unitId, 1L));
//                        product.setUnit(unit);
//
//                        productRepository.save(product);
//                    }
//
//                    ProductBatch batch = productBatchRepository.
//                            findByLotCodeAndProductProduct_id(batchCode, product.getProduct_id())
//                            .orElse(null);
//                    boolean isNewBatch = false;
//
//                    if (batch == null) {
//                        batch = new ProductBatch();
//                        batch.setLotCode(batchCode);
//                        batch.setSerialNumber(serialNum.isEmpty() ? null : serialNum);
//                        batch.setCostPrice(parseBigDecimal(costPrice));
//                        if (parseSupplierId != null) {
//                            Supplier supplier = new Supplier();
//                            supplier.setSupplierId(parseSupplierId);
//                            batch.setSupplier(supplier);
//                        }
//                        batch.setProduct(product);
//
//                        if (!expiryDate.isEmpty()) {
//                            batch.setExpiryDate(parseFlexibleDate(expiryDate));
//                        }
//                        batch = productBatchRepository.save(batch);
//                        isNewBatch = true;
//                    } else {
//                        throw new RuntimeException("Lô hàng '" + batchCode + "' của sản phẩm '"
//                                + sku + "' đã tồn tại! Hệ thống đã hủy quá trình Import.");
//                    }
//
//                    // XỬ LÝ QR CODE
//                    if (isNewBatch) {
//                        String qrContent = sku + "|" + batchCode;
//                        String base64Image = QrCodeUtil.GenerateQRCodeBase64(qrContent, 300, 300);
//
//                        QrCode qrCode = new QrCode();
//                        qrCode.setQrContent(qrContent);
//                        qrCode.setImgPath(base64Image);
//                        qrCode.setReferenceType("BATCH");
//                        qrCode.setReferenceId(batch.getBatchId());
//                        qrCode.setPrinted(false);
//
//                        qrCodeResipotory.save(qrCode);
//                    }
//
//                    // XỬ LÝ TỒN KHO & VỊ TRÍ KỆ
//                    double importQty = 0;
//                    try {
//                        importQty = Double.parseDouble(qtyStr);
//                    } catch (NumberFormatException e) {
//                        importQty = 0;
//                    }
//
//                    if (importQty > 0 && !locationCodeStr.isEmpty()) {
//                        WarehouseLocation location = warehouseLocationRepository
//                                .findByLocationCodeAndWarehouses_WarehouseId(locationCodeStr,warehouseId).orElseGet(
//                                () -> {
//                                    WarehouseLocation newLocation = new WarehouseLocation();
//                                    newLocation.setLocationCode(locationCodeStr);
//                                    com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse defaultWarehouse
//                                            = new com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse();
//                                    defaultWarehouse.setWarehouseId(warehouseId);
//                                    newLocation.setWarehouses(defaultWarehouse);
//
//                                    return warehouseLocationRepository.save(newLocation);
//                                }
//                        );
//
//                        Optional<InventoryLocationBalance> balanceOpt = inventoryLocationBalanceRepository.
//                                findByBatchIdAndLocationId(batch.getBatchId(), location.getLocationId());
//                        InventoryLocationBalance balance;
//
//                        if (balanceOpt.isPresent()) {
//                            balance = balanceOpt.get();
//                            BigDecimal currentQty = balance.getQty() != null ? balance.getQty() : BigDecimal.ZERO;
//                            balance.setQty(currentQty.add(BigDecimal.valueOf(importQty)));
//                        } else {
//                            balance = new InventoryLocationBalance();
//                            balance.setBatchId(batch.getBatchId());
//                            balance.setLocationId(location.getLocationId());
//                            balance.setProductId(product.getProduct_id());
//                            balance.setWarehouseId(location.getWarehouses().getWarehouseId());
//                            balance.setQty(BigDecimal.valueOf(importQty));
//                        }
//                        balance.setUpdateAt(LocalDateTime.now());
//                        inventoryLocationBalanceRepository.save(balance);
//
//                        InventoryHistory history = new InventoryHistory();
//                        history.setTransactionType("INITIAL_IMPORT");
//                        history.setProductId(product.getProduct_id());
//                        history.setBatchId(batch.getBatchId());
//                        history.setToLocationId(location.getLocationId());
//                        history.setQtyChange(BigDecimal.valueOf(importQty));
//                        history.setWarehouseId(location.getWarehouses().getWarehouseId());
//                        history.setCreatedAt(LocalDateTime.now());
//                        inventoryHistoryRepository.save(history);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Lỗi khi đọc file CSV: " + e.getMessage());
//        }
//    }

    private void createNewProductAndBatch(TempImportData temp, Long currentUserId) {
        // Tạo Product mới (nếu chưa có)
        Product product = productRepository.findBySku(temp.getSku());
        if (product == null) {
            product = new Product();
            product.setSku(temp.getSku());
            product.setProductName(temp.getProductName());
            product.setDescription(temp.getDescription() == null || temp.getDescription().isEmpty() ? null : temp.getDescription());
            product.setMinStock(parseBigDecimal(temp.getMinStock()));

            ProductCategory category = new ProductCategory();
            category.setCategoryId(parseLong(temp.getCategoryId(), 1L));
            product.setCategory(category);

            Unit unit = new Unit();
            unit.setUnitId(parseLong(temp.getUnitId(), 1L));
            product.setUnit(unit);

            product = productRepository.save(product);
        }

        // Tạo Batch mới
        ProductBatch batch = new ProductBatch();
        batch.setLotCode(temp.getBatchCode());
        batch.setSerialNumber(temp.getSerialNum() == null || temp.getSerialNum().isEmpty() ? null : temp.getSerialNum());
        batch.setCostPrice(parseBigDecimal(temp.getCostPrice()));

        if (temp.getSupplierId() != null && !temp.getSupplierId().isEmpty()) {
            Supplier supplier = new Supplier();
            supplier.setSupplierId(parseLong(temp.getSupplierId(), null));
            batch.setSupplier(supplier);
        }
        batch.setProduct(product);

        if (temp.getExpiryDate() != null && !temp.getExpiryDate().isEmpty()) {
            batch.setExpiryDate(parseFlexibleDate(temp.getExpiryDate()));
        }
        batch = productBatchRepository.save(batch);

        generateAndSaveQr(product,batch);

        // Xử lý Tồn kho & Vị trí kệ
        double importQty = 0;
        try {
            importQty = Double.parseDouble(temp.getQuantity());
        } catch (NumberFormatException e) {
            importQty = 0;
        }

        if (importQty > 0) {
            String locationCodeStr = temp.getLocationCode() != null ? temp.getLocationCode() : "";
            Long warehouseId = parseLong(temp.getWarehouseId(), 1L);

            if (!locationCodeStr.isEmpty()) {
                WarehouseLocation location = warehouseLocationRepository
                        .findByLocationCodeAndWarehouses_WarehouseId(locationCodeStr, warehouseId).orElseGet(
                                () -> {
                                    WarehouseLocation newLocation = new WarehouseLocation();
                                    newLocation.setLocationCode(locationCodeStr);
                                    com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse defaultWarehouse
                                            = new com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse();
                                    defaultWarehouse.setWarehouseId(warehouseId);
                                    newLocation.setWarehouses(defaultWarehouse);
                                    return warehouseLocationRepository.save(newLocation);
                                }
                        );

                InventoryLocationBalance balance = new InventoryLocationBalance();
                balance.setBatchId(batch.getBatchId());
                balance.setLocationId(location.getLocationId());
                balance.setProductId(product.getProduct_id());
                balance.setWarehouseId(location.getWarehouses().getWarehouseId());
                balance.setQty(BigDecimal.valueOf(importQty));
                balance.setUpdateAt(LocalDateTime.now());
                inventoryLocationBalanceRepository.save(balance);

                InventoryHistory history = new InventoryHistory();
                history.setTransactionType("INITIAL_IMPORT");
                history.setProductId(product.getProduct_id());
                history.setBatchId(batch.getBatchId());
                history.setToLocationId(location.getLocationId());
                history.setQtyChange(BigDecimal.valueOf(importQty));
                history.setWarehouseId(location.getWarehouses().getWarehouseId());
                history.setUserId(currentUserId);
                history.setCreatedAt(LocalDateTime.now());
                inventoryHistoryRepository.save(history);
            }
        }
    }

    private void updateInventoryQuantity(ProductBatch batch, TempImportData temp,Long currentUserId){
        double additionQty = 0;
        try{
            additionQty = Double.parseDouble(temp.getQuantity());
        }
        catch (NumberFormatException e){
            return;
        }
        if(additionQty <= 0 ) return;

        List<InventoryLocationBalance> balances = inventoryLocationBalanceRepository.findByBatchId(batch.getBatchId());
        if (!balances.isEmpty()){
            InventoryLocationBalance balance = balances.get(0);
            BigDecimal currentQty = balance.getQty() != null ? balance.getQty() : BigDecimal.ZERO;

            balance.setQty(currentQty.add(BigDecimal.valueOf(additionQty)));
            balance.setUpdateAt(LocalDateTime.now());
            inventoryLocationBalanceRepository.save(balance);

            InventoryHistory history = new InventoryHistory();
            history.setTransactionType("ADDITION_IMPORT");
            history.setProductId(batch.getProduct().getProduct_id());
            history.setBatchId(batch.getBatchId());
            history.setToLocationId(balance.getLocationId());
            history.setQtyChange(BigDecimal.valueOf(additionQty));
            history.setWarehouseId(balance.getWarehouseId());
            history.setCreatedAt(LocalDateTime.now());
            history.setUserId(currentUserId);
            inventoryHistoryRepository.save(history);
        }
    }

    private boolean isAnyFieldEmpty(TempImportData temp) {
        return isEmpty(temp.getSku()) || isEmpty(temp.getProductName()) ||
                isEmpty(temp.getDescription()) || isEmpty(temp.getMinStock()) ||
                isEmpty(temp.getCategoryId()) || isEmpty(temp.getUnitId()) ||
                isEmpty(temp.getBatchCode()) || isEmpty(temp.getSerialNum()) ||
                isEmpty(temp.getCostPrice()) || isEmpty(temp.getExpiryDate()) ||
                isEmpty(temp.getSupplierId()) || isEmpty(temp.getQuantity()) ||
                isEmpty(temp.getLocationCode()) || isEmpty(temp.getWarehouseId());
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return BigDecimal.ZERO;
        try { return new BigDecimal(value.trim()); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    private Long parseLong(String value, Long defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try { return Long.parseLong(value.trim()); } catch (Exception e) { return defaultValue; }
    }

    private LocalDate parseFlexibleDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        String cleanDate = dateStr.trim();

        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("M-d-yyyy"),
                DateTimeFormatter.ofPattern("MM-dd-yyyy"),
                DateTimeFormatter.ofPattern("d-M-yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("M/d/yyyy"),
                DateTimeFormatter.ofPattern("d/M/yyyy")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(cleanDate, formatter);
            } catch (Exception e) {
            }
        }
        throw new RuntimeException("Hệ thống không hiểu định dạng ngày: " + cleanDate + ". Vui lòng dùng định dạng Năm-Tháng-Ngày hoặc Ngày-Tháng-Năm.");
    }

    private void generateAndSaveQr(Product product, ProductBatch batch) {
        String qrContent = product.getSku() + "|" + batch.getLotCode();
        String base64Image = QrCodeUtil.GenerateQRCodeBase64(qrContent, 300, 300);

        QrCode qrCode = new QrCode();
        qrCode.setQrContent(qrContent);
        qrCode.setImgPath(base64Image);
        qrCode.setReferenceType("BATCH");
        qrCode.setReferenceId(batch.getBatchId());
        qrCode.setPrinted(false);

        qrCodeResipotory.save(qrCode);
    }

    @Transactional
    public void generateManualQr(List<Long> batchIds) {
        for (Long batchId : batchIds) {

            QrCode existingQr = qrCodeResipotory.findByReferenceIdAndReferenceType(batchId, "BATCH");

            if (existingQr == null) {
                ProductBatch batch = productBatchRepository.findById(batchId).orElse(null);

                if (batch != null) {
                    generateAndSaveQr(batch.getProduct(), batch);
                }
            }
        }
    }
}
