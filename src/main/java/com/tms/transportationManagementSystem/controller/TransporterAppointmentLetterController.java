
package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.AppointmentLetterCreateRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.AppointmentLetterListResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.AppointmentLetterResponseRequestDTO;
import com.tms.transportationManagementSystem.entity.TransporterAppointmentLetter;
import com.tms.transportationManagementSystem.service.TransporterAppointmentLetterService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointment-letters")
@RequiredArgsConstructor
public class TransporterAppointmentLetterController {

    private final TransporterAppointmentLetterService appointmentLetterService;

    // Admin: Create / issue a new appointment letter
    @PostMapping
    public ResponseEntity<TransporterAppointmentLetter> create(
            @RequestBody AppointmentLetterCreateRequestDTO request) {

        TransporterAppointmentLetter letter =
                appointmentLetterService.createAppointmentLetter(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(letter);
    }

    // Admin: Get all appointment letters, optionally filter by status
    @GetMapping
    public ResponseEntity<List<AppointmentLetterListResponseDTO>> getAll(
            @RequestParam(required = false) String status) {

        return ResponseEntity.ok(
                appointmentLetterService.getAllAppointmentLetters(status)
        );
    }

    // Get a single appointment letter by ID
    @GetMapping("/{id}")
    public ResponseEntity<TransporterAppointmentLetter> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                appointmentLetterService.getAppointmentLetterById(id)
        );
    }

    // Transporter: Get appointment letters by transporter ID
    @GetMapping("/transporter/{transporterId}")
    public ResponseEntity<List<AppointmentLetterListResponseDTO>> getForTransporter(
            @PathVariable Long transporterId) {

        return ResponseEntity.ok(
                appointmentLetterService.getTransporterAppointmentLetters(transporterId)
        );
    }

    // Transporter: Accept or reject an appointment letter
    @PatchMapping("/{id}/transporter-response/{transporterId}")
    public ResponseEntity<TransporterAppointmentLetter> respond(
            @PathVariable Long id,
            @PathVariable Long transporterId,
            @RequestBody AppointmentLetterResponseRequestDTO request) {

        return ResponseEntity.ok(
                appointmentLetterService.respondToAppointmentLetter(
                        id, transporterId, request
                )
        );
    }

    // Admin: Update appointment letter status
    @PatchMapping("/{id}/status")
    public ResponseEntity<TransporterAppointmentLetter> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                appointmentLetterService.updateAppointmentLetterStatus(id, status)
        );
    }
}
