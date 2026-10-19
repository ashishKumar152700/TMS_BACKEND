package com.tms.transportationManagementSystem.dto.RequestDTO;

import com.tms.transportationManagementSystem.enums.GuardVerificationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuardVerificationRequestDTO {
    private GuardVerificationStatus status;
    private String remarks;
}
