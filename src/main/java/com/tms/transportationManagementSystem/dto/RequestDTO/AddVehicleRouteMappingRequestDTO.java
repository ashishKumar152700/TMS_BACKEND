package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Data;

@Data
public class AddVehicleRouteMappingRequestDTO {

    private Long vehicleId;

    private Long routeId;
}