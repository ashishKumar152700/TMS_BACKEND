package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableRouteResponseDTO {

    private Long routeId;

    private String routeCode;

    private String sourceLocation;

    private String destinationLocation;
}