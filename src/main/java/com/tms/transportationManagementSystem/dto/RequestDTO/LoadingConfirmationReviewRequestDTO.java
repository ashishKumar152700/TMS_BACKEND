package com.tms.transportationManagementSystem.dto.RequestDTO;

import com.tms.transportationManagementSystem.enums.LoadingConfirmationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadingConfirmationReviewRequestDTO {

    @NotNull
    private LoadingConfirmationStatus status;

    private String remarks;
}
