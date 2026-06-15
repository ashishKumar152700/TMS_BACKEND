package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderRouteGroupingResponseDTO {

    private List<RouteOrderGroupResponseDTO> mappedRoutes;

    private List<UnmappedOrderDTO> unmappedOrders;
}