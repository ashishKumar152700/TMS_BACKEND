package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VehicleRequestDTO {

    private String vehicleNumber;
    private String vehicleType;
    private String vehicleCategory;

    private Double maxWeightCapacity;
    private Double maxVolumeCapacity;



    private String gpsDeviceId;

    private String status;


    private Long transporterId;
}