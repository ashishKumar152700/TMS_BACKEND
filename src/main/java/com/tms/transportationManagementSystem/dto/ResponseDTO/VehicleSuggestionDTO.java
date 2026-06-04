package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleSuggestionDTO {

    private String vehicleType;

    private Integer vehiclesRequired;

    private Double maxWeightCapacity;

    private Double maxVolumeCapacity;
}