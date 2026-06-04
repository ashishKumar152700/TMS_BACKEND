package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequestDTO {

    private Long itemId;

    private Integer quantity;

    private Double length;

    private Double breadth;

    private Double height;

    private Double weightPerUnit;
}