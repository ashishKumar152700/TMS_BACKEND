package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransporterRouteResponseDTO {

    private Long id;

    private Long transporterId;

    private String transporterName;

    private Long routeId;

    private String routeCode;

    private Double pricePreference;

    private Boolean active;
}