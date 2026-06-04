package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddOrderDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.OrderDTO;
import com.tms.transportationManagementSystem.entity.Customer;
import com.tms.transportationManagementSystem.entity.Order;
import com.tms.transportationManagementSystem.repository.CustomerRepository;
import com.tms.transportationManagementSystem.repository.OrderRepository;
import com.tms.transportationManagementSystem.service.OrderService;
import enums.OrderStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            CustomerRepository customerRepository
    ) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public OrderDTO createOrder(AddOrderDTO addOrderDTO) {

        Customer customer = customerRepository.findById(
                        addOrderDTO.getCustomerId())
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        Order order = new Order();

        BeanUtils.copyProperties(addOrderDTO, order);

        order.setCustomer(customer);

        calculateOrderValues(order, addOrderDTO);

        order.setOrderNo(
                "ORD-" + UUID.randomUUID().toString().substring(0, 8)
        );

        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        return mapToDTO(savedOrder);
    }

    @Override
    public List<OrderDTO> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found with id " + id
                        )
                );

        return mapToDTO(order);
    }

    @Override
    public OrderDTO updateOrder(Long id, AddOrderDTO addOrderDTO) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found with id " + id
                        )
                );

        Customer customer = customerRepository.findById(
                        addOrderDTO.getCustomerId())
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        BeanUtils.copyProperties(addOrderDTO, order);

        order.setCustomer(customer);

        calculateOrderValues(order, addOrderDTO);

        Order updatedOrder = orderRepository.save(order);

        return mapToDTO(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found with id " + id
                        )
                );

        orderRepository.delete(order);
    }

    @Override
    public List<OrderDTO> getPendingOrders() {

        List<Order> pendingOrders =
                orderRepository.findByStatus(OrderStatus.PENDING);

        return pendingOrders.stream()
                .map(this::mapToDTO)
                .toList();
    }

    private void calculateOrderValues(
            Order order,
            AddOrderDTO addOrderDTO
    ) {

        Double volumePerPackage =
                addOrderDTO.getLength()
                        * addOrderDTO.getBreadth()
                        * addOrderDTO.getHeight();

        Double totalVolume =
                volumePerPackage
                        * addOrderDTO.getNumberOfPackages();

        Double totalWeight =
                addOrderDTO.getWeightPerPackage()
                        * addOrderDTO.getNumberOfPackages();

        order.setVolumePerPackage(volumePerPackage);

        order.setTotalVolume(totalVolume);

        order.setTotalWeight(totalWeight);
    }

    private OrderDTO mapToDTO(Order order) {

        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setId(order.getId());

        orderDTO.setOrderNo(order.getOrderNo());

        if (order.getCustomer() != null) {

            orderDTO.setCustomerId(
                    order.getCustomer().getId()
            );

            orderDTO.setCustomerName(
                    order.getCustomer().getAlphaName()
            );
        }

        orderDTO.setPickupLocation(
                order.getPickupLocation()
        );

        orderDTO.setDeliveryLocation(
                order.getDeliveryLocation()
        );

        orderDTO.setNumberOfPackages(
                order.getNumberOfPackages()
        );

        orderDTO.setLength(
                order.getLength()
        );

        orderDTO.setBreadth(
                order.getBreadth()
        );

        orderDTO.setHeight(
                order.getHeight()
        );

        orderDTO.setVolumePerPackage(
                order.getVolumePerPackage()
        );

        orderDTO.setTotalVolume(
                order.getTotalVolume()
        );

        orderDTO.setWeightPerPackage(
                order.getWeightPerPackage()
        );

        orderDTO.setTotalWeight(
                order.getTotalWeight()
        );

        orderDTO.setStatus(
                order.getStatus().name()
        );

        return orderDTO;
    }
}