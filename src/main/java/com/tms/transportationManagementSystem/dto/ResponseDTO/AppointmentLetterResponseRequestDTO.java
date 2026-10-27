package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentLetterResponseRequestDTO {

    private Boolean accepted;

    private String remarks;

    private LocalDate expectedArrivalDate;

    private LocalTime expectedArrivalTime;

    private String signatureUrl;
}