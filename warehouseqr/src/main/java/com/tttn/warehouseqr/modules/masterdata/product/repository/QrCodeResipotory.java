package com.tttn.warehouseqr.modules.masterdata.product.repository;

import com.tttn.warehouseqr.modules.masterdata.product.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Repository
public interface QrCodeResipotory extends JpaRepository<QrCode, Long> {
    QrCode findByReferenceIdAndReferenceType(long referenceId, String referenceType);
    Optional<QrCode> findByQrContent(String qrContent);
}
