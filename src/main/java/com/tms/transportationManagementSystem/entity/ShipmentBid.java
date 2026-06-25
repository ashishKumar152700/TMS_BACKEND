package com.tms.transportationManagementSystem.entity;

import com.tms.transportationManagementSystem.enums.ShipmentBidStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_bids")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id", nullable = false)
    private Transporter transporter;

    @Column(nullable = false)
    private Double bidAmount;

    @Column(nullable = false)
    private Integer vehicleCount;

    private String remarks;

    private LocalDateTime bidDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentBidStatus bidStatus = ShipmentBidStatus.PENDING;
}