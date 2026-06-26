package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "transporter_shipment_bid_tracker",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"shipmentId", "transporterId"}
        )
)
public class TransporterShipmentBidTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shipmentId;

    private Long transporterId;

    private LocalDateTime bidTime;

}