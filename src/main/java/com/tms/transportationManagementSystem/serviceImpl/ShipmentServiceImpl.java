package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentCreateRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentResponseDTO;
import com.tms.transportationManagementSystem.entity.JdeSalesOrder;
import com.tms.transportationManagementSystem.entity.Shipment;
import com.tms.transportationManagementSystem.entity.ShipmentOrder;
import com.tms.transportationManagementSystem.enums.OrderStatus;
import com.tms.transportationManagementSystem.enums.ShipmentStatus;
import com.tms.transportationManagementSystem.repository.JdeSalesOrderRepository;
import com.tms.transportationManagementSystem.repository.ShipmentOrderRepository;
import com.tms.transportationManagementSystem.repository.ShipmentRepository;
import com.tms.transportationManagementSystem.entity.Vehicle;
import com.tms.transportationManagementSystem.repository.VehicleRepository;
import com.tms.transportationManagementSystem.service.ShipmentService;
import lombok.RequiredArgsConstructor;
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

    private final ShipmentOrderRepository shipmentOrderRepository;

    private final JdeSalesOrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
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
