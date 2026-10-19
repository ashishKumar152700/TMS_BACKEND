package com.tms.transportationManagementSystem.dto.RequestDTO;

import com.tms.transportationManagementSystem.enums.InspectionReviewStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleInspectionReviewRequestDTO {

    private InspectionReviewStatus status;

    private String remarks;
}