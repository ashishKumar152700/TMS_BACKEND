package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class ShipmentBidManagementResponseDTO {

    private Long shipmentId;

    private String shipmentNo;

    private String vehicleType;

    private Double totalWeight;

    private Double totalVolume;

    private Double expectedPrice;

    private LocalDate plannedPickupDate;

    private String shipmentStatus;

    private List<ShipmentOrderResponseDTO> orders;

    private List<ShipmentBidAdminResponseDTO> bids;
}