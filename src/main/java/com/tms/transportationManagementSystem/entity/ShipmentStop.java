package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_stops")
@Getter
@Setter
public class ShipmentStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stopName;

    private Integer sequenceNo;

    private LocalDateTime arrivalTime;

    private LocalDateTime departureTime;

    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;
}