package com.tms.transportationManagementSystem.dto.RequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class LoadingConfirmationRequestDTO {

    @NotNull
    private Long tripId;

    private LocalDateTime loadingStartTime;
    private LocalDateTime loadingEndTime;

    @DecimalMin(value = "0.0")
    private Double actualGrossWeight;

    @DecimalMin(value = "0.0")
    private Double tareWeight;

    @DecimalMin(value = "0.0")
    private Double actualNetWeight;

    @DecimalMin(value = "0.0")
    private Integer packageCount;

    @DecimalMin(value = "0.0")
    private Integer palletCount;

    private String sealNumber;
    private String remarks;

    @NotNull
    @Valid
    private List<LoadingConfirmationItemRequestDTO> items;
}
