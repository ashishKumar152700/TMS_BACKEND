package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableRouteAssignmentResponseDTO {

    private List<AvailableRouteResponseDTO> existingRoutes;

    private List<AvailableRouteResponseDTO> newRoutes;
}