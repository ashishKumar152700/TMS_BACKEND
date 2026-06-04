package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;

@Data
public class JdeSalesOrderLineDTO {

    private Double lineNumber;
    private String itemNumber;
    private String description;
    private Double quantity;
    private String uom;
    private Double unitPrice;
    private Double lineAmount;
}