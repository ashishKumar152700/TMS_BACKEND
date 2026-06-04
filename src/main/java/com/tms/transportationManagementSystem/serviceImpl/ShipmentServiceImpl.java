package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddOrdersToShipmentDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.CreateShipmentDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.SelectVehicleSuggestionDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.OrderResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentDetailsDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleSuggestionDTO;
import com.tms.transportationManagementSystem.entity.Order;
import com.tms.transportationManagementSystem.entity.Shipment;
import com.tms.transportationManagementSystem.entity.ShipmentOrder;
import com.tms.transportationManagementSystem.entity.Vehicle;
import com.tms.transportationManagementSystem.repository.OrderRepository;
import com.tms.transportationManagementSystem.repository.ShipmentOrderRepository;
import com.tms.transportationManagementSystem.repository.ShipmentRepository;
import com.tms.transportationManagementSystem.repository.VehicleRepository;
import com.tms.transportationManagementSystem.service.ShipmentService;
import enums.OrderStatus;
import enums.ShipmentStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ShipmentServiceImpl
        implements ShipmentService {

    private final ShipmentRepository shipmentRepository;

    private final ShipmentOrderRepository
            shipmentOrderRepository;

    private final OrderRepository orderRepository;

    private final VehicleRepository vehicleRepository;
    private ShipmentDetailsDTO mapToShipmentDetailsDTO(
            Shipment shipment
    ) {

        List<ShipmentOrder> shipmentOrders =
                shipmentOrderRepository.findByShipmentId(
                        shipment.getId()
                );

        List<OrderResponseDTO> orders =
                shipmentOrders.stream()
                        .map(shipmentOrder -> {

                            Order order =
                                    orderRepository
                                            .findById(
                                                    shipmentOrder.getOrderId()
                                            )
                                            .orElseThrow();

                            OrderResponseDTO dto =
                                    new OrderResponseDTO();

                            dto.setId(order.getId());
                            dto.setOrderNo(order.getOrderNo());
//                            dto.setCustomerName(order.getCustomerName());
                            dto.setCustomerName(
                                    order.getCustomer() != null
                                            ? order.getCustomer().getAlphaName()
                                            : null
                            );
                            dto.setTotalWeight(order.getTotalWeight());
                            dto.setTotalVolume(order.getTotalVolume());
                            dto.setStatus(order.getStatus().name());

                            return dto;
                        })
                        .toList();

        ShipmentDetailsDTO dto =
                new ShipmentDetailsDTO();

        dto.setId(shipment.getId());
        dto.setShipmentNo(shipment.getShipmentNo());
        dto.setRouteId(shipment.getRouteId());
        dto.setPlannedDate(shipment.getPlannedDate());
        dto.setTotalWeight(shipment.getTotalWeight());
        dto.setTotalVolume(shipment.getTotalVolume());
        dto.setSelectedVehicleType(
                shipment.getSelectedVehicleType()
        );
        dto.setSelectedVehicleCount(
                shipment.getSelectedVehicleCount()
        );
        dto.setStatus(
                shipment.getStatus().name()
        );
        dto.setOrders(orders);

        return dto;
    }


    @Override
    public ShipmentDTO addOrdersToShipment(

            Long shipmentId,

            AddOrdersToShipmentDTO dto
    ) {

        Shipment shipment =
                shipmentRepository
                        .findById(shipmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Shipment not found"
                                )
                        );

        if (shipment.getStatus()
                != ShipmentStatus.PLANNING) {

            throw new RuntimeException(
                    "Orders can only be added when shipment is in PLANNING status"
            );
        }

        double totalWeight =
                shipment.getTotalWeight() != null
                        ? shipment.getTotalWeight()
                        : 0.0;

        double totalVolume =
                shipment.getTotalVolume() != null
                        ? shipment.getTotalVolume()
                        : 0.0;

        for (Long orderId : dto.getOrderIds()) {

            Order order =
                    orderRepository
                            .findById(orderId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Order not found : "
                                                    + orderId
                                    )
                            );

            if (order.getStatus()
                    != OrderStatus.PENDING) {

                throw new RuntimeException(
                        "Only PENDING orders can be added"
                );
            }

            boolean alreadyLinked =
                    shipmentOrderRepository
                            .findByShipmentIdAndOrderId(
                                    shipmentId,
                                    orderId
                            )
                            .isPresent();

            if (alreadyLinked) {

                throw new RuntimeException(
                        "Order already linked to shipment"
                );
            }

            ShipmentOrder shipmentOrder =
                    new ShipmentOrder();

            shipmentOrder.setShipmentId(
                    shipmentId
            );

            shipmentOrder.setOrderId(
                    orderId
            );

            shipmentOrderRepository
                    .save(shipmentOrder);

            order.setStatus(
                    OrderStatus.PLANNED
            );

            orderRepository.save(
                    order
            );

            totalWeight +=
                    order.getTotalWeight();

            totalVolume +=
                    order.getTotalVolume();
        }

        shipment.setTotalWeight(
                totalWeight
        );

        shipment.setTotalVolume(
                totalVolume
        );

        Shipment updatedShipment =
                shipmentRepository.save(
                        shipment
                );

        return mapToDTO(
                updatedShipment
        );
    }

    @Override
    public ShipmentDTO removeOrderFromShipment(

            Long shipmentId,

            Long orderId
    ) {

        Shipment shipment =
                shipmentRepository.findById(
                        shipmentId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Shipment not found"
                        )
                );

        ShipmentOrder shipmentOrder =
                shipmentOrderRepository
                        .findByShipmentIdAndOrderId(
                                shipmentId,
                                orderId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not linked to shipment"
                                )
                        );

        Order order =
                orderRepository.findById(
                        orderId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        )
                );

        shipmentOrderRepository.delete(
                shipmentOrder
        );

        order.setStatus(
                OrderStatus.PENDING
        );

        orderRepository.save(order);

        shipment.setTotalWeight(
                shipment.getTotalWeight()
                        - order.getTotalWeight()
        );

        shipment.setTotalVolume(
                shipment.getTotalVolume()
                        - order.getTotalVolume()
        );

        Shipment updatedShipment =
                shipmentRepository.save(
                        shipment
                );

        return mapToDTO(
                updatedShipment
        );
    }

    @Override
    public List<ShipmentDetailsDTO> getAllShipmentsWithOrders() {

        List<Shipment> shipments =
                shipmentRepository.findAll();

        return shipments.stream()
                .map(this::mapToShipmentDetailsDTO)
                .toList();
    }

    public ShipmentServiceImpl(
            ShipmentRepository shipmentRepository,
            ShipmentOrderRepository shipmentOrderRepository,
            OrderRepository orderRepository,
            VehicleRepository vehicleRepository
    ) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentOrderRepository =
                shipmentOrderRepository;
        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
    }



    @Override
    public ShipmentDetailsDTO getShipmentById(
            Long shipmentId
    ) {

        Shipment shipment = shipmentRepository
                .findById(shipmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Shipment not found"
                        )
                );

        List<ShipmentOrder> shipmentOrders =
                shipmentOrderRepository
                        .findByShipmentId(
                                shipmentId
                        );

        List<OrderResponseDTO> orders =
                shipmentOrders.stream()
                        .map(shipmentOrder -> {

                            Order order =
                                    orderRepository
                                            .findById(
                                                    shipmentOrder.getOrderId()
                                            )
                                            .orElseThrow();

                            OrderResponseDTO dto =
                                    new OrderResponseDTO();

                            dto.setId(order.getId());
                            dto.setOrderNo(order.getOrderNo());
//                            dto.setCustomerName(
//                                    order.getCustomerName()
//                            );
                            dto.setCustomerName(
                                    order.getCustomer() != null
                                            ? order.getCustomer().getAlphaName()
                                            : null
                            );
                            dto.setTotalWeight(
                                    order.getTotalWeight()
                            );
                            dto.setTotalVolume(
                                    order.getTotalVolume()
                            );
                            dto.setStatus(
                                    order.getStatus().name()
                            );

                            return dto;
                        })
                        .toList();

        ShipmentDetailsDTO dto =
                new ShipmentDetailsDTO();

        dto.setId(shipment.getId());
        dto.setShipmentNo(
                shipment.getShipmentNo()
        );
        dto.setRouteId(
                shipment.getRouteId()
        );
        dto.setPlannedDate(
                shipment.getPlannedDate()
        );
        dto.setTotalWeight(
                shipment.getTotalWeight()
        );
        dto.setTotalVolume(
                shipment.getTotalVolume()
        );
        dto.setSelectedVehicleType(
                shipment.getSelectedVehicleType()
        );
        dto.setSelectedVehicleCount(
                shipment.getSelectedVehicleCount()
        );
        dto.setStatus(
                shipment.getStatus().name()
        );

        dto.setOrders(orders);

        return dto;
    }

    @Override
    public ShipmentDTO createShipment(
            CreateShipmentDTO createShipmentDTO
    ) {

        Shipment shipment = new Shipment();

        shipment.setShipmentNo(
                "SHP-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
        );

        shipment.setRouteId(
                createShipmentDTO.getRouteId()
        );

        shipment.setPlannedDate(
                createShipmentDTO.getPlannedDate()
        );

        shipment.setStatus(
                ShipmentStatus.PLANNING
        );

        double shipmentTotalWeight = 0.0;

        double shipmentTotalVolume = 0.0;

        List<Long> orderIds =
                createShipmentDTO.getOrderIds();

        for (Long orderId : orderIds) {

            Order order = orderRepository
                    .findById(orderId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Order not found with id "
                                            + orderId
                            )
                    );

            if (order.getStatus()
                    != OrderStatus.PENDING) {

                throw new IllegalArgumentException(
                        "Only pending orders allowed"
                );
            }

            shipmentTotalWeight +=
                    order.getTotalWeight();

            shipmentTotalVolume +=
                    order.getTotalVolume();
        }

        shipment.setTotalWeight(
                shipmentTotalWeight
        );

        shipment.setTotalVolume(
                shipmentTotalVolume
        );

        Shipment savedShipment =
                shipmentRepository.save(shipment);

        for (Long orderId : orderIds) {

            Order order = orderRepository
                    .findById(orderId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Order not found with id "
                                            + orderId
                            )
                    );

            ShipmentOrder shipmentOrder =
                    new ShipmentOrder();

            shipmentOrder.setShipmentId(
                    savedShipment.getId()
            );

            shipmentOrder.setOrderId(
                    order.getId()
            );

            shipmentOrderRepository
                    .save(shipmentOrder);

            order.setStatus(
                    OrderStatus.PLANNED
            );

            orderRepository.save(order);
        }

        return mapToDTO(savedShipment);
    }

    @Override
    public List<VehicleSuggestionDTO>
    getVehicleSuggestions(Long shipmentId) {

        Shipment shipment =
                shipmentRepository.findById(
                                shipmentId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Shipment not found"
                                )
                        );

        Double shipmentWeight =
                shipment.getTotalWeight() != null
                        ? shipment.getTotalWeight()
                        : 0.0;

        Double shipmentVolume =
                shipment.getTotalVolume() != null
                        ? shipment.getTotalVolume()
                        : 0.0;

        List<Vehicle> vehicles =
                vehicleRepository.findAll();

        return vehicles.stream()

                .filter(vehicle ->

                        vehicle.getMaxWeightCapacity()
                                != null

                                &&

                                vehicle.getMaxVolumeCapacity()
                                        != null
                )

                .map(vehicle -> {

                    double weightRequired =
                            shipmentWeight
                                    / vehicle.getMaxWeightCapacity();

                    double volumeRequired =
                            shipmentVolume
                                    / vehicle.getMaxVolumeCapacity();

                    int vehiclesRequired =
                            (int) Math.ceil(
                                    Math.max(
                                            weightRequired,
                                            volumeRequired
                                    )
                            );

                    VehicleSuggestionDTO dto =
                            new VehicleSuggestionDTO();

                    dto.setVehicleType(
                            vehicle.getVehicleType()
                    );

                    dto.setVehiclesRequired(
                            vehiclesRequired
                    );

                    dto.setMaxWeightCapacity(
                            vehicle.getMaxWeightCapacity()
                    );

                    dto.setMaxVolumeCapacity(
                            vehicle.getMaxVolumeCapacity()
                    );

                    return dto;
                })

                .sorted(
                        Comparator.comparingInt(
                                VehicleSuggestionDTO
                                        ::getVehiclesRequired
                        )
                )

                .limit(3)

                .toList();
    }

    @Override
    public ShipmentDTO selectVehicleSuggestion(

            Long shipmentId,

            SelectVehicleSuggestionDTO dto
    ) {

        Shipment shipment =
                shipmentRepository.findById(
                                shipmentId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Shipment not found"
                                )
                        );

        shipment.setSelectedVehicleType(
                dto.getVehicleType()
        );

        shipment.setSelectedVehicleCount(
                dto.getVehicleCount()
        );

        shipment.setStatus(
                ShipmentStatus.READY_FOR_BIDDING
        );

        Shipment updatedShipment =
                shipmentRepository.save(shipment);

        return mapToDTO(updatedShipment);
    }

    private ShipmentDTO mapToDTO(
            Shipment shipment
    ) {

        ShipmentDTO shipmentDTO =
                new ShipmentDTO();

        shipmentDTO.setId(shipment.getId());

        shipmentDTO.setShipmentNo(
                shipment.getShipmentNo()
        );

        shipmentDTO.setPlannedDate(
                shipment.getPlannedDate()
        );

        shipmentDTO.setTotalWeight(
                shipment.getTotalWeight()
        );

        shipmentDTO.setTotalVolume(
                shipment.getTotalVolume()
        );

        shipmentDTO.setSelectedVehicleType(
                shipment.getSelectedVehicleType()
        );

        shipmentDTO.setSelectedVehicleCount(
                shipment.getSelectedVehicleCount()
        );

        shipmentDTO.setStatus(
                shipment.getStatus().name()
        );

        return shipmentDTO;
    }
}