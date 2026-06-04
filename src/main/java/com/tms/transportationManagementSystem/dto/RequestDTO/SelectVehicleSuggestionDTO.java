package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectVehicleSuggestionDTO {

    private String vehicleType;

    private Integer vehicleCount;
}