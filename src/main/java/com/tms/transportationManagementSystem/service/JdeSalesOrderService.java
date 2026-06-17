package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.AssignRouteRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.AvailableRouteAssignmentResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeSalesOrderResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.OrderRouteGroupingResponseDTO;

import java.util.List;

public interface JdeSalesOrderService {

    List<JdeSalesOrderResponseDTO> getAllSalesOrders();

    OrderRouteGroupingResponseDTO groupOrdersByRoute();

    AvailableRouteAssignmentResponseDTO getAvailableRoutes(
            Long orderId
    );

    String assignRoute(
            AssignRouteRequestDTO request
    );
}