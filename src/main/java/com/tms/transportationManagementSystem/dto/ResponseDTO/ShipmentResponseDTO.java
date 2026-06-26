package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class ShipmentResponseDTO {

    private Long shipmentId;

    @Builder.Default
    private Boolean alreadyBidPlaced = false;

    private String shipmentNo;

    private Double totalWeight;

    private Double totalVolume;

    private String vehicleType;

    private Integer orderCount;

    private String status;

    private List<ShipmentOrderResponseDTO> orders;

    private Double expectedPrice;

    private LocalDate plannedPickupDate;

    private String bidMessage;
}