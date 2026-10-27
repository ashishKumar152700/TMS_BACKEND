package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.OpenBidRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentCreateRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentOrderResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentResponseDTO;
import com.tms.transportationManagementSystem.entity.*;
import com.tms.transportationManagementSystem.enums.OrderStatus;
import com.tms.transportationManagementSystem.enums.ShipmentStatus;
import com.tms.transportationManagementSystem.repository.*;
import com.tms.transportationManagementSystem.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentServiceImpl implements ShipmentService {


    private final ShipmentRepository shipmentRepository;
    private final ShipmentOrderRepository shipmentOrderRepository;
    private final TransporterRepository transporterRepository;
    private final JdeSalesOrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final TransporterShipmentBidTrackerRepository trackerRepository;
    private final SalesOrderLookup salesOrderLookup;

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
    @Transactional(readOnly = true)
    public List<ShipmentResponseDTO> getOpenBids(String token) {

        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        log.debug("OPEN BIDS API | userId={} | name={} | email={} | jdeAddressNo={}",
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getEmail(),
                currentUser.getJdeAddressNumber());

        Long jdeAddressNumber = currentUser.getJdeAddressNumber();

        Transporter transporter = transporterRepository
                .findByAddressNumber(jdeAddressNumber)
                .orElseThrow(() -> new RuntimeException("Transporter not found"));

        Long transporterId = transporter.getId();

        log.debug("OPEN BIDS API | transporterId={}", transporterId);

        List<Shipment> shipments =
                shipmentRepository.findByStatus(ShipmentStatus.BIDDING_OPEN);

        // Is transporter ne kin shipments pe bid lagayi hai - ek hi query
        Set<Long> alreadyBidShipmentIds =
                trackerRepository.findByTransporterId(transporterId)
                        .stream()
                        .map(TransporterShipmentBidTracker::getShipmentId)
                        .collect(Collectors.toSet());

        Map<Long, List<JdeSalesOrder>> ordersByShipmentId =
                loadOrders(shipments);

        return shipments
                .stream()
                .map(shipment -> {

                    boolean alreadyBid =
                            alreadyBidShipmentIds.contains(shipment.getId());

                    log.debug("ShipmentId = {} | ShipmentNo = {} | TransporterId = {} | AlreadyBidPlaced = {}",
                            shipment.getId(),
                            shipment.getShipmentNo(),
                            transporterId,
                            alreadyBid);

                    List<ShipmentOrderResponseDTO> orderDTOs =
                            toOrderDTOs(ordersByShipmentId, shipment.getId());

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
    @Transactional(readOnly = true)
    public List<ShipmentResponseDTO> getAllShipments() {

        List<Shipment> shipments = shipmentRepository.findAll();

        Map<Long, List<JdeSalesOrder>> ordersByShipmentId =
                loadOrders(shipments);

        return shipments.stream().map(shipment -> {

            List<ShipmentOrderResponseDTO> orderDTOs =
                    toOrderDTOs(ordersByShipmentId, shipment.getId());

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


    // Saare shipments ke orders ek saath (pehle har order ke liye findById chalta tha)
    private Map<Long, List<JdeSalesOrder>> loadOrders(List<Shipment> shipments) {

        return salesOrderLookup.ordersByShipmentId(
                shipments.stream()
                        .map(Shipment::getId)
                        .toList()
        );
    }

    private List<ShipmentOrderResponseDTO> toOrderDTOs(
            Map<Long, List<JdeSalesOrder>> ordersByShipmentId,
            Long shipmentId
    ) {

        return ordersByShipmentId.getOrDefault(shipmentId, List.of())
                .stream()
                .map(SalesOrderLookup::toShipmentOrderDTO)
                .toList();
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

        // Kaunse orders pehle se kisi shipment mein hain - ek hi query
        Set<Long> alreadyAssignedOrderIds =
                shipmentOrderRepository.findByOrderIdIn(
                                orders.stream()
                                        .map(JdeSalesOrder::getId)
                                        .toList())
                        .stream()
                        .map(ShipmentOrder::getOrderId)
                        .collect(Collectors.toSet());

        // Validation
        for (JdeSalesOrder order : orders) {


            if (order.getOrderStatus()
                    != OrderStatus.ROUTE_ASSIGNED) {

                throw new RuntimeException(
                        "Order "
                                + order.getOrderNumber()
                                + " is not in ROUTE_ASSIGNED status");
            }

            if (alreadyAssignedOrderIds.contains(order.getId())) {

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

        // Vehicle sirf validate hota hai (shipment pe set nahi hota, pehle jaisa hi)
        if (request.getVehicleId() != null
                && !vehicleRepository.existsById(request.getVehicleId())) {

            throw new RuntimeException(
                    "Vehicle not found : "
                            + request.getVehicleId());
        }

        Shipment shipment = new Shipment();

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

        shipment.setStatus(
                ShipmentStatus.PLANNING);

        Shipment savedShipment =
                shipmentRepository.save(
                        shipment);


        List<ShipmentOrder> shipmentOrders = new ArrayList<>(orders.size());

        for (JdeSalesOrder order : orders) {

            ShipmentOrder shipmentOrder = new ShipmentOrder();
            shipmentOrder.setShipmentId(savedShipment.getId());
            shipmentOrder.setOrderId(order.getId());
            shipmentOrders.add(shipmentOrder);

            log.debug("Updating Order : {} | Before Status : {}",
                    order.getId(), order.getOrderStatus());

            order.setOrderStatus(OrderStatus.SHIPMENT_PLANNED);
        }

        shipmentOrderRepository.saveAll(shipmentOrders);
        orderRepository.saveAll(orders);
        orderRepository.flush();

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
