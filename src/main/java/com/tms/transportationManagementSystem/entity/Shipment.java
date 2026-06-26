package com.tms.transportationManagementSystem.entity;

import com.tms.transportationManagementSystem.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "shipments")
@Getter
@Setter
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shipmentNo;

    private Long routeId;

    private LocalDate plannedDate;

    private Double totalWeight;

    private Double totalVolume;

    private String selectedVehicleType;

    private Integer selectedVehicleCount;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @OneToMany(
            mappedBy = "shipment",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<JdeSalesOrder> orders;

    @OneToMany(
            mappedBy = "shipment",
            cascade = CascadeType.ALL
    )
    private List<ShipmentStop> shipmentStops;

    @Column(name = "expected_price")
    private Double expectedPrice;

    @Column(name = "planned_pickup_date")
    private LocalDate plannedPickupDate;

    @Column(name = "bid_message", length = 500)
    private String bidMessage;
}