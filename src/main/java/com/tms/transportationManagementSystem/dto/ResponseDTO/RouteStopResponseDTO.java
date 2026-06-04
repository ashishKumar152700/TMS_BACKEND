package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteStopResponseDTO {

    private Long id;

    private String stopName;

//    private Integer sequenceNo;

    private Double distanceFromSource;

    private Long routeId;

    private String routeCode;
}