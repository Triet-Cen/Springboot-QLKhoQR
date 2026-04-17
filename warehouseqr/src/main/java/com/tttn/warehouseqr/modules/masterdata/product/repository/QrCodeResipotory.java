package com.tttn.warehouseqr.modules.masterdata.product.repository;

import com.tttn.warehouseqr.modules.masterdata.product.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QrCodeResipotory extends JpaRepository<QrCode, Long> {
    QrCode findByReferenceIdAndReferenceType(long referenceId, String referenceType);
    List<QrCode> findAllByReferenceIdAndReferenceType(long referenceId, String referenceType);
    Optional<QrCode> findByQrContent(String qrContent);
    Optional<QrCode> findByQrContentAndReferenceType(String qrContent, String referenceType);
}
