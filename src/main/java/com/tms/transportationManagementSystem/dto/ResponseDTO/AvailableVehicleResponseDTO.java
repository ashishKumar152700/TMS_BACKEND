package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailableVehicleResponseDTO {

    private Long vehicleId;

    private String vehicleNumber;

    private String vehicleType;

    private Double maxWeightCapacity;

    private Double maxVolumeCapacity;

}