package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderSummaryDTO {

    private Long orderId;
    private String orderNumber;
    private String city;
    private String customerNumber;
}