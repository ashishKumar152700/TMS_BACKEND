package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Data;

@Data
public class AddMultipleRouteStopsRequestDTO {

    private String stopName;

    private Double distanceFromSource;
}