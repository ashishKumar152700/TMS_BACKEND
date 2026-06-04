package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemResponseDTO {

    private Long id;
    private String itemNumber;
    private String description;
    private String description2;
    private String lineType;
    private String stockingType;
    private String thirdItemNumber;
    private Long shortItemNo;
}