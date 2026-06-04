package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String routeCode;

    @Column(nullable = false)
    private String sourceLocation;

    @Column(nullable = false)
    private String destinationLocation;

    private Double distance;

    private String estimatedTime;

    @Builder.Default
    @OneToMany(mappedBy = "route",
            cascade = CascadeType.ALL)
    private List<RouteStop> routeStops =
            new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "route",
            cascade = CascadeType.ALL)
    private List<TransporterRoute> transporterRoutes =
            new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "route",
            cascade = CascadeType.ALL)
    private List<VehicleRouteMapping> vehicleRouteMappings =
            new ArrayList<>();
}