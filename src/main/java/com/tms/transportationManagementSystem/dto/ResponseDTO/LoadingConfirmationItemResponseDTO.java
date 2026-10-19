package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadingConfirmationItemResponseDTO {

    private Long id;
    private Long salesOrderLineId;
    private Long salesOrderId;
    private String orderNumber;
    private String itemNumber;
    private String itemDescription;
    private String uom;
    private Double orderedQuantity;
    private Double loadedQuantity;
    private Double damagedQuantity;
    private Double rejectedQuantity;
    private Double shortQuantity;
    private String damageRemarks;
    private String remarks;
}
