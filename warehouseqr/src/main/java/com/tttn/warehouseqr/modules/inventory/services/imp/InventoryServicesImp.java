package com.tttn.warehouseqr.modules.inventory.services.imp;

import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.inventory.services.InventoryServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class InventoryServicesImp implements InventoryServices {

    private final InventoryLocationBalanceRepository balanceRepository;

    public InventoryServicesImp(InventoryLocationBalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Override
    @Transactional
    public void reduceStock(Long warehouseId, Long locationId, Long productId, Long batchId, BigDecimal qty) {
        InventoryLocationBalance balance = balanceRepository
                .findByWarehouseIdAndLocationIdAndProductIdAndBatchId(warehouseId, locationId, productId, batchId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hàng trong kho nguồn tại vị trí này!"));

        if (balance.getQty().compareTo((qty)) < 0) {
            throw new RuntimeException("Số lượng tồn kho không đủ để điều chuyển!");
        }

        balance.setQty(balance.getQty().subtract(qty));
        balance.setUpdateAt(LocalDateTime.now());
        balanceRepository.save(balance);
    }

    @Override
    public void addStock(Long warehouseId, Long locationId, Long productId, Long batchId, BigDecimal qty) {
        InventoryLocationBalance balance = balanceRepository
                .findByWarehouseIdAndLocationIdAndProductIdAndBatchId(warehouseId, locationId, productId, batchId)
                .orElseGet(() -> {
                    InventoryLocationBalance newBalance = new InventoryLocationBalance();
                    newBalance.setWarehouseId(warehouseId);
                    newBalance.setLocationId(locationId);
                    newBalance.setProductId(productId);
                    newBalance.setBatchId(batchId);
                    newBalance.setQty(BigDecimal.ZERO); // Khởi tạo bằng 0
                    return newBalance;
                });

        // Thực hiện cộng: qty = currentQty + inputQty
        balance.setQty(balance.getQty().add(qty));
        balance.setUpdateAt(LocalDateTime.now());
        balanceRepository.save(balance);
    }
}
