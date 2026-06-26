package com.tms.transportationManagementSystem.entity;

import com.tms.transportationManagementSystem.enums.ShipmentBidStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_bids")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Shipment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Transporter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id", nullable = false)
    private Transporter transporter;

    // Bid Price
    @Column(nullable = false)
    private Double bidAmount;

    // Delivery Commitment (e.g. 2 Days)
    @Column(nullable = false)
    private Integer deliveryDays;

    // Pickup Date
    @Column(nullable = false)
    private LocalDate plannedPickupDate;

    // Optional Message
    @Column(length = 500)
    private String remarks;

    // Bid Time
    @Column(nullable = false)
    private LocalDateTime bidDate;

    // PENDING / ACCEPTED / REJECTED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentBidStatus bidStatus;

    @PrePersist
    public void prePersist() {
        if (bidDate == null) {
            bidDate = LocalDateTime.now();
        }
        if (bidStatus == null) {
            bidStatus = ShipmentBidStatus.PENDING;
        }
    }
}