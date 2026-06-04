package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddShipmentStopDTO {

    private Long shipmentId;

    private String stopName;

    private Integer sequenceNo;
}