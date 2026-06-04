package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Data;

@Data
public class AddTransporterRouteRequestDTO {

    private Long transporterId;

    private Long routeId;

    private Double pricePreference;
}