package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class GatePassResponseDTO {

    private Long id;
    private String gateId;
    private LocalDateTime gateInAt;
    private LocalDateTime gateOutAt;
    private LocalDateTime generatedAt;
    private String status;
    private Long vehicleInspectionId;

    private ShipmentDetails shipment;
    private TripDetails trip;
    private VehicleDetails vehicle;

    @Data
    public static class ShipmentDetails {
        private Long id;
        private String shipmentNo;
        private String status;
        private Long routeId;
        private LocalDate plannedDate;
        private Double totalWeight;
        private Double totalVolume;
        private String selectedVehicleType;
        private Integer selectedVehicleCount;
        private String destinationCity;
    }

    @Data
    public static class TripDetails {

        private Long id;
        private String tripNo;
        private String status;
        private String driverName;
        private String driverMobile;

        private LocalDateTime dispatchDate;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }

    @Data
    public static class VehicleDetails {
        private Long id;
        private String vehicleNumber;
        private String vehicleType;
    }
}