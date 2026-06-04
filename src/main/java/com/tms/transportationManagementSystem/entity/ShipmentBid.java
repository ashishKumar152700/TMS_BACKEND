package com.tms.transportationManagementSystem.entity;

import enums.ShipmentBidStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_bids")
@Getter
@Setter
public class ShipmentBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shipmentId;

    private Long transporterId;

    private Double bidAmount;

    private String remarks;

    @Enumerated(EnumType.STRING)
    private ShipmentBidStatus status;

    private LocalDateTime createdAt;
}