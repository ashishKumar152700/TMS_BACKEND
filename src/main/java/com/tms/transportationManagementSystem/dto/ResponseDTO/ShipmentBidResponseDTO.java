package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentBidResponseDTO {

    private Long bidId;
    private Long shipmentId;
    private Long transporterId;
    private String transporterName;
    private Double bidAmount;
    private Integer deliveryDays;
    private LocalDate plannedPickupDate;
    private String remarks;

    private String bidStatus;
}