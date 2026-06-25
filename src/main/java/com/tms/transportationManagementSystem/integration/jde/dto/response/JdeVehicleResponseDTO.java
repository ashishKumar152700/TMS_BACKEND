package com.tms.transportationManagementSystem.integration.jde.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JdeVehicleResponseDTO {

    private String vehicleNumber;
    private String vehicleType;
    private Double maxWeightCapacity;
    private String totalWeight;
    private String totalVolume;
    private Double maxVolumeCapacity;
    private String gpsDeviceId;
    private String status;
}