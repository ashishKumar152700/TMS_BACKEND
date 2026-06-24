package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleSuggestionResponseDTO {

    private Long vehicleId;
    private String vehicleNumber;
    private String vehicleType;

    private Double maxWeightCapacity;
    private Double maxVolumeCapacity;

    private Double orderWeight;
    private Double orderVolume;

    private String reason;
}