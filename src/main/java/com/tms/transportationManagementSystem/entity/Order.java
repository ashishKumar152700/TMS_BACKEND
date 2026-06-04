package com.tms.transportationManagementSystem.entity;

import enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNo;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String pickupLocation;

    private String deliveryLocation;

    private Integer numberOfPackages;

    private Double length;

    private Double breadth;

    private Double height;

    private Double volumePerPackage;

    private Double weightPerPackage;

    private Double totalVolume;

    private Double totalWeight;

    private LocalDate pickupDate;

    private LocalDate deliveryDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}