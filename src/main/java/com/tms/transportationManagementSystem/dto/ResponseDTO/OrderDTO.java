package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {

    private Long id;

    private String orderNo;

    private Long customerId;

    private String customerName;

    private String pickupLocation;

    private String deliveryLocation;

    private Integer numberOfPackages;

    private Double length;

    private Double breadth;

    private Double height;

    private Double volumePerPackage;

    private Double totalVolume;

    private Double weightPerPackage;

    private Double totalWeight;

    private String status;
}