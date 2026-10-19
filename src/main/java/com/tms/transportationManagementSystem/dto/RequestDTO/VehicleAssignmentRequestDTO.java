package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleAssignmentRequestDTO {

    private Long shipmentId;

    private Long vehicleId;

    private String driverName;

    private String driverMobile;

}