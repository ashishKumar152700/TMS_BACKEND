package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transporter_routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransporterRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double pricePreference;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "transporter_id")
    private Transporter transporter;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
}