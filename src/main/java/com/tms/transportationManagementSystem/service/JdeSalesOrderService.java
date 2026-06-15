package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeSalesOrderResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.OrderRouteGroupingResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.RouteOrderGroupResponseDTO;

import java.util.List;

public interface JdeSalesOrderService {

    List<JdeSalesOrderResponseDTO> getAllSalesOrders();

//    List<RouteOrderGroupResponseDTO> groupOrdersByRoute();
public OrderRouteGroupingResponseDTO groupOrdersByRoute();
}