package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OpenBidRequestDTO {

    private Double expectedPrice;

    private LocalDate plannedPickupDate;

    private String bidMessage;

}