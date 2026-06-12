package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeSalesOrderResponseDTO;

import java.util.List;

public interface JdeSalesOrderService {

    List<JdeSalesOrderResponseDTO> getAllSalesOrders();
}