package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ShipmentBidRequestDTO {

    private Long shipmentId;

    private Double bidAmount;

    private Integer deliveryDays;

    private LocalDate plannedPickupDate;

    private String remarks;

}