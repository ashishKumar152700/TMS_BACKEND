package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShipmentCreateRequestDTO {

    private List<Long> orderIds;

    private Long routeId;

    private Long vehicleId;

    private String selectedVehicleType;

    private Integer selectedVehicleCount;
}