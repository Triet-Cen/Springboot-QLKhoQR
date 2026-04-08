package com.tttn.warehouseqr.modules.outbound.request;

import com.tttn.warehouseqr.modules.outbound.dto.OutboundItemDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutboundRequestDTO {
    private String outboundCode;
    private Long warehouseId;
    private Long salesOrderId;
    private Long customerId;
    private List<OutboundItemDTO> items;
}
