package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.LoadingConfirmationRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.LoadingConfirmationReviewRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.LoadingConfirmationItemDetailsDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.LoadingConfirmationResponseDTO;

import java.util.List;

public interface LoadingConfirmationService {

    LoadingConfirmationResponseDTO getByTripId(Long tripId);

    LoadingConfirmationResponseDTO getById(Long id);

    LoadingConfirmationResponseDTO create(LoadingConfirmationRequestDTO request);

    LoadingConfirmationResponseDTO updateDraft(Long id, LoadingConfirmationRequestDTO request);

    LoadingConfirmationResponseDTO submit(Long id);

    List<LoadingConfirmationItemDetailsDTO> getItemsByTripId(Long tripId);


    LoadingConfirmationResponseDTO review(
            Long id,
            LoadingConfirmationReviewRequestDTO request
    );
}
