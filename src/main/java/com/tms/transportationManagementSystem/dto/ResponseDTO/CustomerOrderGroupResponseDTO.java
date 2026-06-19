package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderGroupResponseDTO {

    private String customerNumber;
    private String customerName;
    private List<JdeSalesOrderResponseDTO> orders;
}