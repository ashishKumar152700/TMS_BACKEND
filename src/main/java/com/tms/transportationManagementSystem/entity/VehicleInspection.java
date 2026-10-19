package com.tms.transportationManagementSystem.entity;

import com.tms.transportationManagementSystem.enums.InspectionResult;
import com.tms.transportationManagementSystem.enums.InspectionReviewStatus;
import com.tms.transportationManagementSystem.enums.GuardVerificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "vehicle_inspections")
@Getter
@Setter
public class VehicleInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;


    // 1. Transporter & Vehicle Identification

    private String carrierCode;

    private LocalDate arrivalDate;

    private LocalTime arrivalTime;

    private String gateEntryNo;


    // 2. Driver Inspection

    private String drivingLicenseNumber;

    private LocalDate licenseValidity;

    private String driverIdGatePass;


    // Driver Documents

    @Lob
    @Column(name = "driving_license_file")
    private byte[] drivingLicenseFile;

    private String drivingLicenseFileName;

    private String drivingLicenseContentType;


    @Lob
    @Column(name = "driver_id_gate_pass_file")
    private byte[] driverIdGatePassFile;

    private String driverIdGatePassFileName;

    private String driverIdGatePassContentType;


    // 3. Vehicle Documents

    private Boolean rcAvailable;

    private Boolean insuranceAvailable;

    private Boolean fitnessCertificateAvailable;

    private Boolean pucAvailable;

    private Boolean permitAvailable;

    private Boolean driverLicenseAvailable;

    private Boolean taxDocumentValid;


    // Vehicle Document Files

    @Lob
    @Column(name = "rc_file")
    private byte[] rcFile;

    private String rcFileName;

    private String rcContentType;


    @Lob
    @Column(name = "insurance_file")
    private byte[] insuranceFile;

    private String insuranceFileName;

    private String insuranceContentType;


    @Lob
    @Column(name = "fitness_certificate_file")
    private byte[] fitnessCertificateFile;

    private String fitnessCertificateFileName;

    private String fitnessCertificateContentType;


    @Lob
    @Column(name = "puc_file")
    private byte[] pucFile;

    private String pucFileName;

    private String pucContentType;


    @Lob
    @Column(name = "permit_file")
    private byte[] permitFile;

    private String permitFileName;

    private String permitContentType;


    @Lob
    @Column(name = "tax_document_file")
    private byte[] taxDocumentFile;

    private String taxDocumentFileName;

    private String taxDocumentContentType;


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


    // 6. Vehicle Capacity Check

    private Double vehicleCapacity;

    private Double shipmentWeight;

    private String capacityResult;


    // Technical Inspection Result

    @Enumerated(EnumType.STRING)
    private InspectionResult inspectionResult;


    // Guard Verification at vehicle arrival

    @Enumerated(EnumType.STRING)
    @Column(name = "guard_verification_status", nullable = false)
    private GuardVerificationStatus guardVerificationStatus = GuardVerificationStatus.PENDING;

    @Column(name = "guard_verification_remarks", length = 1000)
    private String guardVerificationRemarks;

    @Column(name = "guard_verified_at")
    private LocalDateTime guardVerifiedAt;


    // Admin Review

    @Enumerated(EnumType.STRING)
    private InspectionReviewStatus reviewStatus;

    @Column(length = 1000)
    private String reviewRemarks;

    private LocalDateTime reviewedAt;


    // General Remarks

    @Column(length = 1000)
    private String remarks;

    private LocalDateTime inspectedAt;

    private LocalDateTime createdAt;
}