package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponseDTO {

    private Long id;

    private String routeCode;

    private String sourceLocation;

    private String destinationLocation;

    private Double distance;

    private String estimatedTime;

    private List<RouteStopResponseDTO> stops;
}