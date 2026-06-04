package com.tms.transportationManagementSystem.integration.jde.dto.response;

import lombok.Data;

@Data
public class JdeItemResponseDTO {

    private String itemNumber;
    private String description;
    private String description2;
    private String lineType;
    private String stockingType;
    private String thirdItemNumber;
    private Long shortItemNo;
}