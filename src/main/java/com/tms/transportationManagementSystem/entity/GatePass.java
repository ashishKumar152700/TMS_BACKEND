
package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "gate_passes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_gate_pass_gate_id",
                        columnNames = "gate_id"
                ),
                @UniqueConstraint(
                        name = "uk_gate_pass_inspection",
                        columnNames = "vehicle_inspection_id"
                )
        }
)
@Getter
@Setter
public class GatePass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gate_id", nullable = false, unique = true, length = 50)
    private String gateId;

    @Column(name = "gate_in_at")
    private LocalDateTime gateInAt;

    @Column(name = "gate_out_at")
    private LocalDateTime gateOutAt;

    @Column(name = "vehicle_inspection_id", nullable = false, unique = true)
    private Long vehicleInspectionId;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "shipment_id", nullable = false)
    private Long shipmentId;

    @Column(nullable = false, length = 30)
    private String status = "GENERATED";

    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    public void onCreate() {
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
    }
}
