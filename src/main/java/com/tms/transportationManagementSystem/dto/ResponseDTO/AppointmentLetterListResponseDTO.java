
package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentLetterListResponseDTO {

    private Long id;
    private String appointmentNumber;
    private String driverName;
    private String routeName;

    private LocalDate reportingDate;
    private LocalTime reportingTime;
    private String status;

    private Long tripId;

    private String pickupLocation;
    private String reportingGate;
    private String loadingBay;
    private String loadingWindow;

    private VehicleDetailsDTO vehicle;
    private ShipmentDetailsDTO shipment;
}
