package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;
import java.util.List;

@Data
public class CustomerSalesOrderResponseDTO {

    private Long addressNumber;
    private String customerName;
    private List<JdeSalesOrderResponseDTO> salesOrders;
}