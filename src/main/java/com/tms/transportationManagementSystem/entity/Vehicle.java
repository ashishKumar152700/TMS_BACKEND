package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "jde_vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String vehicleNumber;

    private String vehicleType;
    private String vehicleCategory;
    private Double maxWeightCapacity;
    private Double maxVolumeCapacity;
    private String gpsDeviceId;
    private String status;

    @ManyToOne
    @JoinColumn(name = "transporter_id")
    private Transporter transporter;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<VehicleRouteMapping> vehicleRouteMappings;
}