package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_route_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRouteMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private JdeSalesOrder order;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    private String assignmentType; // AUTO / MANUAL

    private String assignedBy;

    private LocalDateTime assignedAt;
}