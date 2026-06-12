
package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.ResponseDTO.CustomerSalesOrderResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeCustomerResponseDTO;

import java.util.List;

public interface CustomerService {

    List<JdeCustomerResponseDTO> getAllCustomers();

    CustomerSalesOrderResponseDTO getCustomerSalesOrders(Long addressNumber);
    List<JdeCustomerResponseDTO> getAllCustomersWithOrders();
}