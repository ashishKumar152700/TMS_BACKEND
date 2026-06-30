package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.OpenBidRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentCreateRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.AppliedBidResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentOrderResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentResponseDTO;
import com.tms.transportationManagementSystem.entity.*;
import com.tms.transportationManagementSystem.enums.OrderStatus;
import com.tms.transportationManagementSystem.enums.ShipmentStatus;
import com.tms.transportationManagementSystem.repository.*;
import com.tms.transportationManagementSystem.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentServiceImpl implements ShipmentService {


    private final ShipmentRepository shipmentRepository;
//    private final JwtService jwtService;
    private final ShipmentOrderRepository shipmentOrderRepository;
    private final TransporterRepository transporterRepository;
    private final JdeSalesOrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final TransporterShipmentBidTrackerRepository trackerRepository;
    private final UserRepository userRepository;

    @Override
    public ShipmentResponseDTO openBid(
            Long shipmentId,
            OpenBidRequestDTO request) {

        Shipment shipment =
                shipmentRepository.findById(shipmentId)
                        .orElseThrow(() ->
                                new RuntimeException("Shipment not found"));

        shipment.setStatus(
                ShipmentStatus.BIDDING_OPEN);

        shipment.setExpectedPrice(
                request.getExpectedPrice());

        shipment.setPlannedPickupDate(
                request.getPlannedPickupDate());

        shipment.setBidMessage(
                request.getBidMessage());

        Shipment saved =
                shipmentRepository.save(shipment);

        int orderCount =
                shipmentOrderRepository
                        .findByShipmentId(saved.getId())
                        .size();

        return ShipmentResponseDTO.builder()
                .shipmentId(saved.getId())
                .shipmentNo(saved.getShipmentNo())
                .totalWeight(saved.getTotalWeight())
                .totalVolume(saved.getTotalVolume())
                .vehicleType(saved.getSelectedVehicleType())
                .orderCount(orderCount)
                .status(saved.getStatus().name())
                .expectedPrice(saved.getExpectedPrice())
                .plannedPickupDate(saved.getPlannedPickupDate())
                .bidMessage(saved.getBidMessage())
                .build();
    }

    @Override
    public List<Shipment> findByStatus(ShipmentStatus status) {
        return List.of();
    }

    @Override
    public List<ShipmentResponseDTO> getOpenBids(String token) {

        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        System.out.println("\n================ OPEN BIDS API ================");
        System.out.println("User Id           : " + currentUser.getId());
        System.out.println("User Name         : " + currentUser.getName());
        System.out.println("Email             : " + currentUser.getEmail());
        System.out.println("JDE Address No    : " + currentUser.getJdeAddressNumber());

        Long jdeAddressNumber = currentUser.getJdeAddressNumber();

        Transporter transporter = transporterRepository
                .findByAddressNumber(jdeAddressNumber)
                .orElseThrow(() -> new RuntimeException("Transporter not found"));

        Long transporterId = transporter.getId();

        System.out.println("Transporter Id    : " + transporterId);
        System.out.println("===============================================\n");

        return shipmentRepository.findByStatus(ShipmentStatus.BIDDING_OPEN)
                .stream()
                .map(shipment -> {

                    boolean alreadyBid = trackerRepository
                            .existsByShipmentIdAndTransporterId(
                                    shipment.getId(),
                                    transporterId
                            );

                    System.out.println(
                            "ShipmentId = " + shipment.getId()
                                    + " | ShipmentNo = " + shipment.getShipmentNo()
                                    + " | TransporterId = " + transporterId
                                    + " | AlreadyBidPlaced = " + alreadyBid
                    );

                    List<ShipmentOrder> shipmentOrders =
                            shipmentOrderRepository.findByShipmentId(shipment.getId());

                    List<ShipmentOrderResponseDTO> orderDTOs =
                            shipmentOrders.stream()
                                    .map(so -> orderRepository.findById(so.getOrderId()).orElse(null))
                                    .filter(Objects::nonNull)
                                    .map(order -> ShipmentOrderResponseDTO.builder()
                                            .orderId(order.getId())
                                            .orderNumber(order.getOrderNumber())
                                            .customerNumber(order.getCustomerNumber())
                                            .customerName(order.getShipToCustomerName())
                                            .city(order.getShipToCity())
                                            .state(order.getShipToState())
                                            .totalWeight(order.getTotalWeight())
                                            .totalVolume(order.getTotalVolume())
                                            .orderDate(order.getOrderDate())
                                            .orderStatus(order.getOrderStatus().name())
                                            .build())
                                    .toList();

                    return ShipmentResponseDTO.builder()
                            .shipmentId(shipment.getId())
                            .shipmentNo(shipment.getShipmentNo())
                            .vehicleType(shipment.getSelectedVehicleType())
                            .totalWeight(shipment.getTotalWeight())
                            .totalVolume(shipment.getTotalVolume())
                            .expectedPrice(shipment.getExpectedPrice())
                            .plannedPickupDate(shipment.getPlannedPickupDate())
                            .bidMessage(shipment.getBidMessage())
                            .status(shipment.getStatus().name())
                            .alreadyBidPlaced(alreadyBid)
                            .orderCount(orderDTOs.size())
                            .orders(orderDTOs)
                            .build();
                })
                .toList();
    }

    @Override
    public List<ShipmentResponseDTO> getAllShipments() {

        List<Shipment> shipments = shipmentRepository.findAll();

        return shipments.stream().map(shipment -> {

            List<ShipmentOrder> shipmentOrders =
                    shipmentOrderRepository.findByShipmentId(shipment.getId());

            List<ShipmentOrderResponseDTO> orderDTOs =
                    shipmentOrders.stream()
                            .map(ShipmentOrder::getOrderId)
                            .map(orderId -> orderRepository.findById(orderId).orElse(null))
                            .filter(Objects::nonNull)
                            .map(order -> ShipmentOrderResponseDTO.builder()
                                    .orderId(order.getId())
                                    .orderNumber(order.getOrderNumber())
                                    .customerNumber(order.getCustomerNumber())
                                    .customerName(order.getShipToCustomerName())
                                    .city(order.getShipToCity())
                                    .state(order.getShipToState())
                                    .totalWeight(order.getTotalWeight())
                                    .totalVolume(order.getTotalVolume())
                                    .orderDate(order.getOrderDate())
                                    .orderStatus(order.getOrderStatus().name())
                                    .build())
                            .toList();

            return ShipmentResponseDTO.builder()
                    .shipmentId(shipment.getId())
                    .shipmentNo(shipment.getShipmentNo())
                    .totalWeight(shipment.getTotalWeight())
                    .totalVolume(shipment.getTotalVolume())
                    .vehicleType(shipment.getSelectedVehicleType())
                    .orderCount(orderDTOs.size())
                    .status(shipment.getStatus().name())
                    .orders(orderDTOs)
                    .build();

        }).toList();
    }



    @Override
    public ShipmentResponseDTO createShipment(
            ShipmentCreateRequestDTO request) {

        List<JdeSalesOrder> orders =
                orderRepository.findAllById(
                        request.getOrderIds());

        if (orders.isEmpty()) {
            throw new RuntimeException(
                    "No orders found");
        }

        // Validation
        for (JdeSalesOrder order : orders) {


            if (order.getOrderStatus()
                    != OrderStatus.ROUTE_ASSIGNED) {

                throw new RuntimeException(
                        "Order "
                                + order.getOrderNumber()
                                + " is not in ROUTE_ASSIGNED status");
            }

            if (shipmentOrderRepository
                    .existsByOrderId(order.getId())) {

                throw new RuntimeException(
                        "Order already assigned to shipment : "
                                + order.getOrderNumber());
            }
        }

        double totalWeight =
                orders.stream()
                        .map(JdeSalesOrder::getTotalWeight)
                        .filter(Objects::nonNull)
                        .mapToDouble(BigDecimal::doubleValue)
                        .sum();

        double totalVolume =
                orders.stream()
                        .map(JdeSalesOrder::getTotalVolume)
                        .filter(Objects::nonNull)
                        .mapToDouble(BigDecimal::doubleValue)
                        .sum();

        Shipment shipment = new Shipment();

        shipment.setRouteId(
                request.getRouteId());

        shipment.setSelectedVehicleType(
                request.getSelectedVehicleType());

        shipment.setSelectedVehicleCount(
                request.getSelectedVehicleCount() != null
                        ? request.getSelectedVehicleCount()
                        : 1);

        if (request.getVehicleId() != null) {

            Vehicle vehicle =
                    vehicleRepository.findById(
                                    request.getVehicleId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Vehicle not found : "
                                                    + request.getVehicleId()));

//            shipment.setVehicle(vehicle);
        }
        shipment.setShipmentNo(
                "SHP-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8));

        shipment.setPlannedDate(
                LocalDate.now());

        shipment.setTotalWeight(totalWeight);

        shipment.setTotalVolume(totalVolume);

        shipment.setRouteId(
                request.getRouteId());

        shipment.setSelectedVehicleType(
                request.getSelectedVehicleType());

        shipment.setSelectedVehicleCount(
                request.getSelectedVehicleCount() != null
                        ? request.getSelectedVehicleCount()
                        : 1);

        if (request.getVehicleId() != null) {

            Vehicle vehicle =
                    vehicleRepository.findById(
                                    request.getVehicleId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Vehicle not found"));

//            shipment.setVehicle(vehicle);
        }

        shipment.setStatus(
                ShipmentStatus.PLANNING);

        Shipment savedShipment =
                shipmentRepository.save(
                        shipment);


        for (JdeSalesOrder order : orders) {

            ShipmentOrder shipmentOrder = new ShipmentOrder();
            shipmentOrder.setShipmentId(savedShipment.getId());
            shipmentOrder.setOrderId(order.getId());

            shipmentOrderRepository.save(shipmentOrder);

            System.out.println("=================================");
            System.out.println("Updating Order : " + order.getId());
            System.out.println("Before Status : " + order.getOrderStatus());

            order.setOrderStatus(OrderStatus.SHIPMENT_PLANNED);

            JdeSalesOrder updatedOrder = orderRepository.save(order);

            orderRepository.flush();

            System.out.println("After Status : " + updatedOrder.getOrderStatus());
            System.out.println("=================================");
        }

        return ShipmentResponseDTO.builder()
                .shipmentId(
                        savedShipment.getId())
                .shipmentNo(
                        savedShipment.getShipmentNo())
                .totalWeight(
                        savedShipment.getTotalWeight())
                .totalVolume(
                        savedShipment.getTotalVolume())
                .orderCount(
                        orders.size())
                .status(
                        savedShipment.getStatus().name())
                .build();
    }

}
