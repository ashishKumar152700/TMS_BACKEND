package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ShipmentBidAdminResponseDTO {

    private Long bidId;

    private Long transporterId;

    private String transporterName;

    private Double bidAmount;

    private Integer deliveryDays;

    private LocalDate plannedPickupDate;

    private String remarks;

    private LocalDateTime bidDate;

    private String bidStatus;
}