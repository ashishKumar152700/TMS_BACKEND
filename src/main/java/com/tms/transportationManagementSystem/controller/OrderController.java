package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddOrderDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.OrderDTO;
import com.tms.transportationManagementSystem.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderDTO createOrder(@RequestBody AddOrderDTO addOrderDTO) {

        return orderService.createOrder(addOrderDTO);
    }

    @GetMapping
    public List<OrderDTO> getAllOrders() {

        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable Long id) {

        return orderService.getOrderById(id);
    }

    @GetMapping("/pending")
    public List<OrderDTO> getPendingOrders() {

        return orderService.getPendingOrders();
    }

    @PutMapping("/{id}")
    public OrderDTO updateOrder(
            @PathVariable Long id,
            @RequestBody AddOrderDTO addOrderDTO
    ) {

        return orderService.updateOrder(id, addOrderDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {

        orderService.deleteOrder(id);

        return "Order deleted successfully";
    }
}