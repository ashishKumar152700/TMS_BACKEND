package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Data;

@Data
public class AddRouteStopRequestDTO {

    private String stopName;

//    private Integer sequenceNo;

    private Double distanceFromSource;

    private Long routeId;
}