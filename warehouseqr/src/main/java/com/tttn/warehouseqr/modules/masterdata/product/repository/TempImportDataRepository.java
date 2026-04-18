package com.tttn.warehouseqr.modules.masterdata.product.repository;

import com.tttn.warehouseqr.modules.masterdata.product.entity.TempImportData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TempImportDataRepository extends JpaRepository<TempImportData, Long> {
    List<TempImportData> findByImportSessionId(String sesstionId);

}
