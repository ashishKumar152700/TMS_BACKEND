package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class AppliedBidResponseDTO {

    private Long bidId;

    private Long shipmentId;

    private String shipmentNo;

    private Double bidAmount;

    private Integer deliveryDays;

    private LocalDate plannedPickupDate;

    private String remarks;

    private LocalDateTime bidDate;

    private String bidStatus;

    private Double expectedPrice;

    private String vehicleType;

    private Double totalWeight;

    private Double totalVolume;

    private String shipmentStatus;
    private List<ShipmentOrderResponseDTO> orders;
}