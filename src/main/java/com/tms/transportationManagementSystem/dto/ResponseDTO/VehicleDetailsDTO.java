
package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;

@Data
public class VehicleDetailsDTO {

    private Long id;
    private String vehicleNumber;
    private String vehicleType;
    private String vehicleCategory;
    private Double maxWeightCapacity;
    private Double maxVolumeCapacity;
    private String gpsDeviceId;
    private String status;
    private String weightUnit;
    private String volumeUnit;

    private Long transporterId;
    private String transporterName;
}
