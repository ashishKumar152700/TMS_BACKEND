package com.tms.transportationManagementSystem.dto.ResponseDTO;

import com.tms.transportationManagementSystem.enums.LoadingConfirmationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class LoadingConfirmationResponseDTO {

    private Long id;
    private Long tripId;
    private String tripNo;
    private Long shipmentId;
    private String shipmentNo;
    private String vehicleNumber;
    private String driverName;
    private String driverMobile;
    private LoadingConfirmationStatus status;

    private LocalDateTime loadingStartTime;
    private LocalDateTime loadingEndTime;

    private Double plannedShipmentWeight;
    private Double actualGrossWeight;
    private Double tareWeight;
    private Double actualNetWeight;

    private Integer packageCount;
    private Integer palletCount;
    private String sealNumber;
    private String remarks;

    private String confirmedBy;
    private LocalDateTime confirmedAt;
    private String supervisorRemarks;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<LoadingConfirmationItemResponseDTO> items;
}
