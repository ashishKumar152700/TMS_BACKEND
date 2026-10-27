package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "transporter_appointment_letters",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "appointment_number")
        }
)
@Getter
@Setter
public class TransporterAppointmentLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_number", nullable = false, unique = true)
    private String appointmentNumber;

    private LocalDate appointmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id", nullable = false)
    private Transporter transporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(length = 1000)
    private String remarks;

    // Shipment / JDE details
    private String salesOrderNumbers;
    private String company;
    private String branchPlant;
    private String businessUnit;
    private String routeCode;

    // Delivery information
    private String customer;
    private String deliveryLocation;
    private String route;
    private String expectedTransitTime;
    private LocalDate expectedDeliveryDate;

    // Pickup information: Admin fills these fields
    @Column(nullable = false)
    private String pickupLocation;

    @Column(nullable = false)
    private String reportingGate;

    @Column(nullable = false)
    private String loadingBay;

    @Column(nullable = false)
    private LocalDate reportingDate;

    @Column(nullable = false)
    private LocalTime reportingTime;

    @Column(nullable = false)
    private String loadingWindow;

    // Cargo information
    private String productCategory;
    private Double totalWeight;
    private Double totalVolume;
    private Integer numberOfPackages;

    @Column(length = 1000)
    private String specialHandling;

    // Freight information
    private BigDecimal approvedFreight;
    private String freightType;
    private BigDecimal additionalCharges;

    // Transporter response
    private LocalDate expectedArrivalDate;
    private LocalTime expectedArrivalTime;
    private Boolean transporterAcceptance;

    @Column(length = 1000)
    private String transporterRemarks;

    @Column(name = "transporter_signature_url", columnDefinition = "TEXT")
    private String transporterSignatureUrl;

    @Column(nullable = false, length = 30)
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        if (appointmentDate == null) {
            appointmentDate = LocalDate.now();
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = "ISSUED";
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}