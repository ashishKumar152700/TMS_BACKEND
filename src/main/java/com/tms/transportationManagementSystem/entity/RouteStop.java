package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "route_stops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteStop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stopName;

    private Integer sequenceNo;

    private Double distanceFromSource;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
}