package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRouteGroupingResponseDTO {

    private List<CustomerRouteGroupResponseDTO> customers;

    private List<UnmappedOrderDTO> unmappedOrders;
}