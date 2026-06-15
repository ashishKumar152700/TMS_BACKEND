package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
public class VehicleResponseDTO {

    private Long id;
    private String vehicleNumber;
    private String vehicleType;
    private Double maxWeightCapacity;
    private Double maxVolumeCapacity;
    private String gpsDeviceId;
    private String status;
}