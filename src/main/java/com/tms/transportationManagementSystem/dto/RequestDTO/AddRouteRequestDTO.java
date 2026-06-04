package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Data;

@Data
public class AddRouteRequestDTO {

    private String routeCode;

    private String sourceLocation;

    private String destinationLocation;

    private Double distance;

    private String estimatedTime;
}