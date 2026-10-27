package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TransporterAppointmentLetterResponseDTO {

    private Long id;
    private String appointmentNumber;
    private String driverName;
    private String vehicleNumber;
    private String routeName;
    private LocalDate reportingDate;
    private LocalTime reportingTime;
    private String status;
    private Long tripId;
}