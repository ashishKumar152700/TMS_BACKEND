package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Data;

@Data
public class AddVehicleRequestDTO {

    private String vehicleNumber;

    private String vehicleType;

    private Double maxWeightCapacity;

    private Double maxVolumeCapacity;

    private String driverName;

    private String driverPhone;

    private Long transporterId;
}