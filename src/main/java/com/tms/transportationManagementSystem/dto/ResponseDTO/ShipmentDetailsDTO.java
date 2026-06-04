package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ShipmentDetailsDTO {

    private Long id;

    private String shipmentNo;

    private Long routeId;

    private LocalDate plannedDate;

    private Double totalWeight;

    private Double totalVolume;

    private String selectedVehicleType;

    private Integer selectedVehicleCount;

    private String status;

    private List<OrderResponseDTO> orders;
}