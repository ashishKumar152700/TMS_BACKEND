package com.tms.transportationManagementSystem.dto.RequestDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadingConfirmationItemRequestDTO {

    @NotNull
    private Long salesOrderLineId;

    @NotNull
    @DecimalMin(value = "0.0")
    private Double loadedQuantity;

    @NotNull
    @DecimalMin(value = "0.0")
    private Double damagedQuantity;

    @NotNull
    @DecimalMin(value = "0.0")
    private Double rejectedQuantity;

    private String damageRemarks;
    private String remarks;
}
