package com.tms.transportationManagementSystem.dto.ResponseDTO;

import com.tms.transportationManagementSystem.enums.InspectionResult;
import com.tms.transportationManagementSystem.enums.InspectionReviewStatus;
import com.tms.transportationManagementSystem.enums.GuardVerificationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class VehicleInspectionResponseDTO {

    private Long inspectionId;

    private Long tripId;

    private String tripNo;

    private Long shipmentId;

    private String shipmentNo;


    // Vehicle

    private Long vehicleId;

    private String vehicleNumber;

    private String vehicleType;

    private Double vehicleCapacity;

    private String weightUnit;


    // Driver

    private String driverName;

    private String driverMobile;


    // 1. Identification

    private String carrierCode;

    private LocalDate arrivalDate;

    private LocalTime arrivalTime;

    private String gateEntryNo;


    // 2. Driver Inspection

    private String drivingLicenseNumber;

    private LocalDate licenseValidity;

    private String driverIdGatePass;


    // Driver Documents

    private String drivingLicenseFileName;

    private String drivingLicenseFileUrl;

    private String driverIdGatePassFileName;

    private String driverIdGatePassFileUrl;


    // 3. Vehicle Documents

    private Boolean rcAvailable;

    private Boolean insuranceAvailable;

    private Boolean fitnessCertificateAvailable;

    private Boolean pucAvailable;

    private Boolean permitAvailable;

    private Boolean driverLicenseAvailable;

    private Boolean taxDocumentValid;


    // Vehicle Document Files

    private String rcFileName;

    private String rcFileUrl;

    private String insuranceFileName;

    private String insuranceFileUrl;

    private String fitnessCertificateFileName;

    private String fitnessCertificateFileUrl;

    private String pucFileName;

    private String pucFileUrl;

    private String permitFileName;

    private String permitFileUrl;

    private String taxDocumentFileName;

    private String taxDocumentFileUrl;


    // 4. Physical Vehicle Inspection

    private Boolean bodyConditionOk;

    private Boolean rustCorrosionOk;

    private Boolean damageDentsOk;

    private Boolean tyresOk;

    private Boolean lightsOk;

    private Boolean indicatorsOk;

    private Boolean mirrorsOk;

    private Boolean numberPlateOk;

    private Boolean windshieldOk;

    private Boolean wipersOk;


    // Safety

    private Boolean brakeOk;

    private Boolean handBrakeOk;

    private Boolean reverseHornOk;

    private Boolean hornOk;

    private Boolean fireExtinguisherAvailable;

    private Boolean wheelChocksAvailable;

    private Boolean firstAidKitAvailable;

    private Boolean reflectiveTapeAvailable;


    // 5. Container / Body Inspection

    private Boolean cleanlinessOk;

    private Boolean previousLoad;

    private Boolean contaminationFree;

    private Boolean leakageFree;

    private Boolean damageFree;

    private Boolean sharpEdgesFree;

    private Boolean pestInsectsFree;


    // 6. Capacity

    private Double shipmentWeight;

    private String capacityResult;


    // Technical Inspection Result

    private InspectionResult inspectionResult;


    // Guard Verification

    private GuardVerificationStatus guardVerificationStatus;

    private String guardVerificationRemarks;

    private LocalDateTime guardVerifiedAt;

    private String gateId;

    private LocalDateTime gateInAt;
    private LocalDateTime gateOutAt;
    // Admin Review

    private InspectionReviewStatus reviewStatus;

    private String reviewRemarks;

    private LocalDateTime reviewedAt;


    // General Remarks

    private String remarks;

    private LocalDateTime inspectedAt;
}