package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AddOrderDTO {

    private Long customerId;

    private String pickupLocation;

    private String deliveryLocation;

    private Integer numberOfPackages;

    private Double length;

    private Double breadth;

    private Double height;

    private Double weightPerPackage;

    private LocalDate pickupDate;

    private LocalDate deliveryDate;
}