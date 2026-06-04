package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CreateShipmentDTO {

    private Long routeId;
    private LocalDate plannedDate;
    private List<Long> orderIds;
}