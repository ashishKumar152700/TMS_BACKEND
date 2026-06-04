package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleRouteMappingResponseDTO {

    private Long id;

    private Long vehicleId;

    private String vehicleNumber;

    private Long routeId;

    private String routeCode;

    private Boolean active;
}