package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignRouteRequestDTO {

    private Long orderId;

    private Long routeId;

//    private String assignedBy;
}
