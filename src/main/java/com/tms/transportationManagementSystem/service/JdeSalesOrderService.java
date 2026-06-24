package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.AssignRouteRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.*;

import java.util.List;

public interface JdeSalesOrderService {

    List<JdeSalesOrderResponseDTO> getAllSalesOrders();



    AvailableRouteAssignmentResponseDTO getAvailableRoutes(
            Long orderId
    );

    String assignRoute(
            AssignRouteRequestDTO request
    );
    CustomerOrderGroupingResponseDTO groupOrdersByCustomer();

    CustomerRouteGroupingResponseDTO
    groupOrdersByCustomerAndRoute();



    VehicleSuggestionResponseDTO suggestVehicle(Long orderId);
}