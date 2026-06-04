package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleResponseDTO {

    private Long id;

    private String vehicleNumber;

    private String vehicleType;

    private Double maxWeightCapacity;

    private Double maxVolumeCapacity;

    private String driverName;

    private String driverPhone;

    private String status;

    private Long transporterId;

    private String transporterName;
}