package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentLetterCreateRequestDTO {

    private Long tripId;

    private String remarks;

    // Admin-entered pickup information
    private String pickupLocation;
    private String reportingGate;
    private String loadingBay;
    private LocalDate reportingDate;
    private LocalTime reportingTime;
    private String loadingWindow;

    // Only used if these values are not available in existing records
    private String businessUnit;
    private String productCategory;
    private Integer numberOfPackages;
    private String specialHandling;

    // Optional freight configuration
    private String freightType;
    private Double additionalCharges;
}