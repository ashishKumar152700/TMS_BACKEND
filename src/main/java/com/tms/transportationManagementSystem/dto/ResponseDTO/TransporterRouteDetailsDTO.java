package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TransporterRouteDetailsDTO {

    private Long routeId;

    private String routeCode;

    private String sourceLocation;

    private String destinationLocation;

    private Double distance;

    private String estimatedTime;

    private List<RouteStopResponseDTO> stops;
}