package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RouteOrderGroupResponseDTO {

    private Long routeId;
    private String routeCode;
    private String sourceLocation;
    private String destinationLocation;

    private List<OrderSummaryDTO> orders;
}