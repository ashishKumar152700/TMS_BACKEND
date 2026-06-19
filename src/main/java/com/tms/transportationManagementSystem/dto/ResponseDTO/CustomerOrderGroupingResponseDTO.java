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
public class CustomerOrderGroupingResponseDTO {

    private List<CustomerOrderGroupResponseDTO> customers;
}