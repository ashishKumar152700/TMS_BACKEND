package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class VehicleInspectionRequestDTO {

    private Long tripId;

    // 1. Transporter & Vehicle Identification

    private String carrierCode;

    private LocalDate arrivalDate;

    private LocalTime arrivalTime;

    private String gateEntryNo;


    // 2. Driver Inspection

    private String drivingLicenseNumber;

    private LocalDate licenseValidity;

    private String driverIdGatePass;

    private MultipartFile drivingLicenseFile;

    private MultipartFile driverIdGatePassFile;


    // 3. Vehicle Documents

    private Boolean rcAvailable;

    private Boolean insuranceAvailable;

    private Boolean fitnessCertificateAvailable;

    private Boolean pucAvailable;

    private Boolean permitAvailable;

    private Boolean driverLicenseAvailable;

    private Boolean taxDocumentValid;

    private MultipartFile rcFile;

    private MultipartFile insuranceFile;

    private MultipartFile fitnessCertificateFile;

    private MultipartFile pucFile;

    private MultipartFile permitFile;

    private MultipartFile taxDocumentFile;


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


    // Remarks

    private String remarks;
}