package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteOrderGroupResponseDTO {

    private Long routeId;
    private String routeCode;
    private String sourceLocation;
    private String destinationLocation;
    private List<RouteStopResponseDTO> routeStops;
    private List<JdeSalesOrderResponseDTO> orders;
}