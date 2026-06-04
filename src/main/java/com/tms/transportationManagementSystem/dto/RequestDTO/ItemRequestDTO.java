package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Data;

@Data
public class ItemRequestDTO {

    private String itemNumber;
    private String description;
    private String description2;
    private String lineType;
    private String stockingType;
    private String thirdItemNumber;
    private Long shortItemNo;
}