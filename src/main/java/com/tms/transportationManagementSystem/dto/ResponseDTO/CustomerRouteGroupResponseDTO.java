package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRouteGroupResponseDTO {

    private String customerNumber;

    private String customerName;

    private List<RouteOrderGroupResponseDTO> routes;
}