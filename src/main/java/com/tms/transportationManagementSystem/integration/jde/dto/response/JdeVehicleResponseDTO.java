package com.tms.transportationManagementSystem.integration.jde.dto.response;

import lombok.Data;

@Data
public class JdeVehicleResponseDTO {

    private String vehicleNumber;
    private String vehicleType;
    private Double maxWeightCapacity;
    private Double maxVolumeCapacity;
    private String gpsDeviceId;
    private String status;
}