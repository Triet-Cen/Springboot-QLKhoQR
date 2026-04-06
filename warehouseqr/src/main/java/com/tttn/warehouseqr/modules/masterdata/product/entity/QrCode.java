package com.tttn.warehouseqr.modules.masterdata.product.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "qr_codes")
public class QrCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qr_code_id")
    private long qrCodeId;

    @Column(name = "qr_content", nullable = false,columnDefinition = "TEXT")
    private String qrContent;

    @Column(name = "img_path",columnDefinition = "TEXT")
    private String imgPath;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "reference_id")
    private long referenceId;

    @Column(name = "is_printed")
    private boolean isPrinted = false;

    public QrCode() {
    }

    public QrCode(long qrCodeId, String qrContent, String imgPath,
                  String referenceType, long referenceId, boolean isPrinted) {
        this.qrCodeId = qrCodeId;
        this.qrContent = qrContent;
        this.imgPath = imgPath;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.isPrinted = isPrinted;
    }

    public long getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(long qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    public String getQrContent() {
        return qrContent;
    }

    public void setQrContent(String qrContent) {
        this.qrContent = qrContent;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(long referenceId) {
        this.referenceId = referenceId;
    }

    public boolean isPrinted() {
        return isPrinted;
    }

    public void setPrinted(boolean printed) {
        isPrinted = printed;
    }
}
