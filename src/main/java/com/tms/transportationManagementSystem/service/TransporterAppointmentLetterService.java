
package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.AppointmentLetterCreateRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.AppointmentLetterListResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.AppointmentLetterResponseRequestDTO;
import com.tms.transportationManagementSystem.entity.TransporterAppointmentLetter;

import java.util.List;

public interface TransporterAppointmentLetterService {

    TransporterAppointmentLetter createAppointmentLetter(
            AppointmentLetterCreateRequestDTO request
    );

    TransporterAppointmentLetter getAppointmentLetterById(Long id);

    List<AppointmentLetterListResponseDTO> getTransporterAppointmentLetters(
            Long transporterId
    );

    TransporterAppointmentLetter respondToAppointmentLetter(
            Long id,
            Long transporterId,
            AppointmentLetterResponseRequestDTO request
    );

    TransporterAppointmentLetter updateAppointmentLetterStatus(
            Long id,
            String status
    );

    List<AppointmentLetterListResponseDTO> getAllAppointmentLetters(
            String status
    );
}
