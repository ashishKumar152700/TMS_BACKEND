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
    private String vehicleCategory;
    private String registrationNumber;
    private String ownershipType;

    private Double maxWeightCapacity;
    private Double maxVolumeCapacity;

    private Double length;
    private Double width;
    private Double height;

    private String driverName;
    private String driverPhone;

    private String gpsDeviceId;

    private String status;

    private LocalDate registrationExpiryDate;
    private LocalDate insuranceExpiryDate;
    private LocalDate permitExpiryDate;
    private LocalDate fitnessExpiryDate;
    private LocalDate pollutionExpiryDate;

    private Long transporterId;
    private String transporterName;
}