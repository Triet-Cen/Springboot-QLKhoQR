package com.tttn.warehouseqr.modules.masterdata.product.service.impl;

import com.tttn.warehouseqr.modules.masterdata.category.entity.ProductCategory;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch;
import com.tttn.warehouseqr.modules.masterdata.product.entity.QrCode;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.QrCodeResipotory;
import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;
import com.tttn.warehouseqr.modules.masterdata.unit.entity.Unit;
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
import java.util.List;

@Service
public class ImportQrService {
    private final ProductRepository productRepository;
    private final ProductBatchRepository productBatchRepository;
    private final QrCodeResipotory qrCodeResipotory;

    public ImportQrService(ProductRepository productRepository,
                           ProductBatchRepository productBatchRepository,
                           QrCodeResipotory qrCodeResipotory) {
        this.productRepository = productRepository;
        this.productBatchRepository = productBatchRepository;
        this.qrCodeResipotory = qrCodeResipotory;
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
