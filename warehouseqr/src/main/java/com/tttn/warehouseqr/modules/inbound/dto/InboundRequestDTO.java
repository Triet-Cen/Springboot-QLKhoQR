package com.tttn.warehouseqr.modules.inbound.dto;

import com.tttn.warehouseqr.modules.inbound.request.InboundItemRequestDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InboundRequestDTO {
    private String inboundReceiptCode;
    private Long purchaseOrderId;
    private Long supplierId;
    private Long warehouseId;
    private String deliveryNoteCode;
    private Double price;


    private List<InboundItemRequestDTO> items;
}
