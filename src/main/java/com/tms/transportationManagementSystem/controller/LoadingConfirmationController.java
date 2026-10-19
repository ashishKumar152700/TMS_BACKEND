package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.LoadingConfirmationRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.LoadingConfirmationReviewRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.LoadingConfirmationItemDetailsDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.LoadingConfirmationResponseDTO;
import com.tms.transportationManagementSystem.service.LoadingConfirmationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loading-confirmations")
@RequiredArgsConstructor
public class LoadingConfirmationController {

    private final LoadingConfirmationService loadingConfirmationService;

    @GetMapping("/trip/{tripId}")
    public LoadingConfirmationResponseDTO getByTripId(@PathVariable Long tripId) {
        return loadingConfirmationService.getByTripId(tripId);
    }

    @GetMapping("/{id}")
    public LoadingConfirmationResponseDTO getById(@PathVariable Long id) {
        return loadingConfirmationService.getById(id);
    }

    @PostMapping
    public LoadingConfirmationResponseDTO create(
            @Valid @RequestBody LoadingConfirmationRequestDTO request
    ) {
        return loadingConfirmationService.create(request);
    }

    @PutMapping("/{id}")
    public LoadingConfirmationResponseDTO updateDraft(
            @PathVariable Long id,
            @Valid @RequestBody LoadingConfirmationRequestDTO request
    ) {
        return loadingConfirmationService.updateDraft(id, request);
    }

    @PostMapping("/{id}/submit")
    public LoadingConfirmationResponseDTO submit(@PathVariable Long id) {
        return loadingConfirmationService.submit(id);
    }

    @PostMapping("/{id}/review")
    public LoadingConfirmationResponseDTO review(
            @PathVariable Long id,
            @Valid @RequestBody LoadingConfirmationReviewRequestDTO request
    ) {
        return loadingConfirmationService.review(id, request);
    }

    @GetMapping("/trip/{tripId}/items")
    public List<LoadingConfirmationItemDetailsDTO> getItemsByTripId(
            @PathVariable Long tripId) {

        return loadingConfirmationService.getItemsByTripId(tripId);
    }
}
