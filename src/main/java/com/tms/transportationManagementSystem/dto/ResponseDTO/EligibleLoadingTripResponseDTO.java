package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;

@Data
public class EligibleLoadingTripResponseDTO {

    private Long tripId;
    private String tripNo;
    private String tripStatus;

    private Long inspectionId;
    private String guardVerificationStatus;

    private Long shipmentId;
    private String shipmentNo;
    private String shipmentStatus;
    private Double shipmentWeight;
    private Double shipmentVolume;

    private Long vehicleId;
    private String vehicleNumber;
    private String vehicleType;
    private Double vehicleCapacity;
    private String weightUnit;

    private String driverName;
    private String driverMobile;

    private Long loadingConfirmationId;
    private String loadingConfirmationStatus;
}