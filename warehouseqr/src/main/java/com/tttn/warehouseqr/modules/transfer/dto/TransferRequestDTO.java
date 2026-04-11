package com.tttn.warehouseqr.modules.transfer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TransferRequestDTO {
    private Long fromWarehouseId;
    private Long toWarehouseId;
    private Long outboundReceiptId;
    private List<TransferItemDTO> items;
}
