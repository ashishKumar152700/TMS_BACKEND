
package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ShipmentDetailsDTO {

    private Long id;
    private String shipmentNo;
    private Long routeId;
    private LocalDate plannedDate;
    private Double totalWeight;
    private Double totalVolume;
    private String selectedVehicleType;
    private Integer selectedVehicleCount;
    private String status;

    private Double expectedPrice;
    private LocalDate plannedPickupDate;
    private String bidMessage;
}
