
package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;

@Data
public class LoadingConfirmationItemDetailsDTO {

    private Long salesOrderLineId;
    private Long salesOrderId;
    private String orderNumber;
    private String lineNumber;

    private String itemNumber;
    private String description;
    private Double orderedQuantity;
    private String uom;
}
