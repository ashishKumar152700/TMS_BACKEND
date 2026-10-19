package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderLineResponseDTO {

    private Long id;

    private Double lineNumber;

    private String itemNumber;

    private String description;

    private Double quantity;

    private String uom;

    private Double unitPrice;

    private Double lineAmount;
}