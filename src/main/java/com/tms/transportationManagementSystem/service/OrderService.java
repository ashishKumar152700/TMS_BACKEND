package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddOrderDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.OrderDTO;

import java.util.List;

public interface OrderService {

    OrderDTO createOrder(AddOrderDTO addOrderDTO);

    List<OrderDTO> getAllOrders();

    OrderDTO getOrderById(Long id);

    OrderDTO updateOrder(Long id, AddOrderDTO addOrderDTO);

    void deleteOrder(Long id);

    List<OrderDTO> getPendingOrders();
}