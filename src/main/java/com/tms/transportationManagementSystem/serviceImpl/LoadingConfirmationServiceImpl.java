package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.LoadingConfirmationItemRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.LoadingConfirmationRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.LoadingConfirmationReviewRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.LoadingConfirmationItemDetailsDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.LoadingConfirmationItemResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.LoadingConfirmationResponseDTO;
import com.tms.transportationManagementSystem.entity.*;
import com.tms.transportationManagementSystem.enums.LoadingConfirmationStatus;
import com.tms.transportationManagementSystem.repository.*;
import com.tms.transportationManagementSystem.service.LoadingConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tms.transportationManagementSystem.repository.JdeSalesOrderLineRepository;
import com.tms.transportationManagementSystem.repository.JdeSalesOrderRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class LoadingConfirmationServiceImpl implements LoadingConfirmationService {

    private final LoadingConfirmationRepository loadingConfirmationRepository;
    private final TripRepository tripRepository;
    private final ShipmentOrderRepository shipmentOrderRepository;
    private final JdeSalesOrderRepository salesOrderRepository;
    private final JdeSalesOrderLineRepository salesOrderLineRepository;
    private final JdeSalesOrderLineRepository jdeSalesOrderLineRepository;
    private final JdeSalesOrderRepository jdeSalesOrderRepository;


    @Override
    @Transactional(readOnly = true)
    public List<LoadingConfirmationItemDetailsDTO> getItemsByTripId(
            Long tripId) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() ->
                        new RuntimeException("Trip not found: " + tripId));

        Shipment shipment = trip.getShipment();

        if (shipment == null) {
            throw new RuntimeException(
                    "Shipment not found for trip: " + tripId);
        }

        List<ShipmentOrder> shipmentOrders =
                shipmentOrderRepository.findByShipmentId(shipment.getId());

        Map<Long, LoadingConfirmationItemDetailsDTO> uniqueItems =
                new LinkedHashMap<>();

        for (ShipmentOrder shipmentOrder : shipmentOrders) {

            Long orderId = shipmentOrder.getOrderId();

            if (orderId == null) {
                continue;
            }

            JdeSalesOrder salesOrder = jdeSalesOrderRepository
                    .findById(orderId)
                    .orElse(null);

            if (salesOrder == null) {
                continue;
            }

            List<JdeSalesOrderLine> lines =
                    jdeSalesOrderLineRepository
                            .findBySalesOrder_Id(salesOrder.getId());

            for (JdeSalesOrderLine line : lines) {

                LoadingConfirmationItemDetailsDTO dto =
                        new LoadingConfirmationItemDetailsDTO();

                dto.setSalesOrderLineId(line.getId());
                dto.setSalesOrderId(salesOrder.getId());
                dto.setOrderNumber(salesOrder.getOrderNumber());
                dto.setLineNumber(
                        line.getLineNumber() == null
                                ? null
                                : line.getLineNumber().toString());
                dto.setItemNumber(line.getItemNumber());
                dto.setDescription(line.getDescription());
                dto.setOrderedQuantity(line.getQuantity());
                dto.setUom(line.getUom());

                uniqueItems.putIfAbsent(line.getId(), dto);
            }
        }

        return new ArrayList<>(uniqueItems.values());
    }


    @Override
    @Transactional(readOnly = true)
    public LoadingConfirmationResponseDTO getByTripId(Long tripId) {
        LoadingConfirmation confirmation = loadingConfirmationRepository.findByTripId(tripId)
                .orElseThrow(() -> new RuntimeException(
                        "Loading confirmation not found for trip: " + tripId));
        return mapResponse(confirmation);
    }

    @Override
    @Transactional(readOnly = true)
    public LoadingConfirmationResponseDTO getById(Long id) {
        return mapResponse(findConfirmation(id));
    }

    @Override
    public LoadingConfirmationResponseDTO create(LoadingConfirmationRequestDTO request) {
        if (request.getTripId() == null) {
            throw new RuntimeException("Trip ID is required");
        }
        if (loadingConfirmationRepository.existsByTripId(request.getTripId())) {
            throw new RuntimeException("Loading confirmation already exists for this trip");
        }

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new RuntimeException("Trip not found: " + request.getTripId()));

        if (trip.getShipment() == null) {
            throw new RuntimeException("Shipment is not associated with this trip");
        }

        validateTimes(request);
        validateWeights(request);

        LoadingConfirmation confirmation = new LoadingConfirmation();
        confirmation.setTrip(trip);
        copyHeaderFields(request, confirmation);
        confirmation.setStatus(LoadingConfirmationStatus.DRAFT);
        replaceItems(confirmation, request.getItems(), trip.getShipment().getId());

        return mapResponse(loadingConfirmationRepository.save(confirmation));
    }

    @Override
    public LoadingConfirmationResponseDTO updateDraft(
            Long id,
            LoadingConfirmationRequestDTO request
    ) {
        LoadingConfirmation confirmation = findConfirmation(id);
        if (confirmation.getStatus() != LoadingConfirmationStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT loading confirmations can be updated");
        }
        if (request.getTripId() == null
                || !request.getTripId().equals(confirmation.getTrip().getId())) {
            throw new RuntimeException("Trip ID cannot be changed");
        }

        validateTimes(request);
        validateWeights(request);
        copyHeaderFields(request, confirmation);
        replaceItems(confirmation, request.getItems(), confirmation.getTrip().getShipment().getId());

        return mapResponse(loadingConfirmationRepository.save(confirmation));
    }

    @Override
    public LoadingConfirmationResponseDTO submit(Long id) {
        LoadingConfirmation confirmation = findConfirmation(id);
        if (confirmation.getStatus() != LoadingConfirmationStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT loading confirmations can be submitted");
        }
        if (confirmation.getItems() == null || confirmation.getItems().isEmpty()) {
            throw new RuntimeException("At least one item is required");
        }

        for (LoadingConfirmationItem item : confirmation.getItems()) {
            if (item.getLoadedQuantity() == null || item.getDamagedQuantity() == null
                    || item.getRejectedQuantity() == null) {
                throw new RuntimeException("Loaded, damaged and rejected quantities are required");
            }
            if (item.getDamagedQuantity() > item.getLoadedQuantity()) {
                throw new RuntimeException("Damaged quantity cannot exceed loaded quantity for item "
                        + item.getItemNumber());
            }
        }

        confirmation.setStatus(LoadingConfirmationStatus.SUBMITTED);
        confirmation.setConfirmedAt(LocalDateTime.now());
        return mapResponse(loadingConfirmationRepository.save(confirmation));
    }

    @Override
    public LoadingConfirmationResponseDTO review(
            Long id,
            LoadingConfirmationReviewRequestDTO request
    ) {
        LoadingConfirmation confirmation = findConfirmation(id);
        if (confirmation.getStatus() != LoadingConfirmationStatus.SUBMITTED) {
            throw new RuntimeException("Only SUBMITTED loading confirmations can be reviewed");
        }
        if (request.getStatus() != LoadingConfirmationStatus.APPROVED
                && request.getStatus() != LoadingConfirmationStatus.REJECTED) {
            throw new RuntimeException("Review status must be APPROVED or REJECTED");
        }
        if (request.getStatus() == LoadingConfirmationStatus.REJECTED
                && (request.getRemarks() == null || request.getRemarks().isBlank())) {
            throw new RuntimeException("Remarks are required when rejecting loading confirmation");
        }

        confirmation.setStatus(request.getStatus());
        confirmation.setSupervisorRemarks(request.getRemarks());
        confirmation.setReviewedAt(LocalDateTime.now());
        // reviewedBy/confirmedBy should be populated from your actual authenticated principal.
        return mapResponse(loadingConfirmationRepository.save(confirmation));
    }

    private LoadingConfirmation findConfirmation(Long id) {
        return loadingConfirmationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loading confirmation not found: " + id));
    }

    private void validateTimes(LoadingConfirmationRequestDTO request) {
        if (request.getLoadingStartTime() != null
                && request.getLoadingEndTime() != null
                && request.getLoadingEndTime().isBefore(request.getLoadingStartTime())) {
            throw new RuntimeException("Loading end time cannot be before loading start time");
        }
    }

    private void validateWeights(LoadingConfirmationRequestDTO request) {
        if (request.getActualGrossWeight() != null && request.getTareWeight() != null
                && request.getActualGrossWeight() < request.getTareWeight()) {
            throw new RuntimeException("Gross weight cannot be less than tare weight");
        }
        if (request.getActualNetWeight() != null
                && request.getActualNetWeight() < 0) {
            throw new RuntimeException("Net weight cannot be negative");
        }
    }

    private void copyHeaderFields(
            LoadingConfirmationRequestDTO request,
            LoadingConfirmation confirmation
    ) {
        confirmation.setLoadingStartTime(request.getLoadingStartTime());
        confirmation.setLoadingEndTime(request.getLoadingEndTime());
        confirmation.setActualGrossWeight(request.getActualGrossWeight());
        confirmation.setTareWeight(request.getTareWeight());

        if (request.getActualGrossWeight() != null && request.getTareWeight() != null) {
            confirmation.setActualNetWeight(
                    request.getActualGrossWeight() - request.getTareWeight());
        } else {
            confirmation.setActualNetWeight(request.getActualNetWeight());
        }

        confirmation.setPackageCount(request.getPackageCount());
        confirmation.setPalletCount(request.getPalletCount());
        confirmation.setSealNumber(request.getSealNumber());
        confirmation.setRemarks(request.getRemarks());
    }

    private void replaceItems(
            LoadingConfirmation confirmation,
            List<LoadingConfirmationItemRequestDTO> itemRequests,
            Long shipmentId
    ) {
        if (itemRequests == null || itemRequests.isEmpty()) {
            throw new RuntimeException("At least one loading item is required");
        }

        Set<Long> seenLineIds = new HashSet<>();
        List<LoadingConfirmationItem> newItems = new ArrayList<>();

        List<ShipmentOrder> shipmentOrders = shipmentOrderRepository.findByShipmentId(shipmentId);
        Set<Long> allowedOrderIds = new HashSet<>();
        for (ShipmentOrder shipmentOrder : shipmentOrders) {
            allowedOrderIds.add(shipmentOrder.getOrderId());
        }

        for (LoadingConfirmationItemRequestDTO itemRequest : itemRequests) {
            if (itemRequest.getSalesOrderLineId() == null) {
                throw new RuntimeException("Sales order line ID is required");
            }
            if (!seenLineIds.add(itemRequest.getSalesOrderLineId())) {
                throw new RuntimeException("Duplicate sales order line ID: "
                        + itemRequest.getSalesOrderLineId());
            }

            JdeSalesOrderLine line = salesOrderLineRepository.findById(
                            itemRequest.getSalesOrderLineId())
                    .orElseThrow(() -> new RuntimeException(
                            "Sales order line not found: " + itemRequest.getSalesOrderLineId()));

            JdeSalesOrder order = line.getSalesOrder();
            if (order == null || !allowedOrderIds.contains(order.getId())) {
                throw new RuntimeException("Sales order line does not belong to this shipment");
            }

            double ordered = line.getQuantity() == null ? 0.0 : line.getQuantity();
            double loaded = nonNegative(itemRequest.getLoadedQuantity(), "loadedQuantity");
            double damaged = nonNegative(itemRequest.getDamagedQuantity(), "damagedQuantity");
            double rejected = nonNegative(itemRequest.getRejectedQuantity(), "rejectedQuantity");

            if (loaded > ordered) {
                throw new RuntimeException("Loaded quantity cannot exceed ordered quantity for item "
                        + line.getItemNumber());
            }
            if (damaged > loaded) {
                throw new RuntimeException("Damaged quantity cannot exceed loaded quantity for item "
                        + line.getItemNumber());
            }
            if (rejected > ordered - loaded) {
                throw new RuntimeException("Rejected quantity cannot exceed quantity not loaded for item "
                        + line.getItemNumber());
            }

            LoadingConfirmationItem item = new LoadingConfirmationItem();
            item.setLoadingConfirmation(confirmation);
            item.setSalesOrderLine(line);
            item.setSalesOrderId(order.getId());
            item.setOrderNumber(order.getOrderNumber());
            item.setItemNumber(line.getItemNumber());
            item.setItemDescription(line.getDescription());
            item.setUom(line.getUom());
            item.setOrderedQuantity(ordered);
            item.setLoadedQuantity(loaded);
            item.setDamagedQuantity(damaged);
            item.setRejectedQuantity(rejected);
            item.setShortQuantity(Math.max(0.0, ordered - loaded - rejected));
            item.setDamageRemarks(itemRequest.getDamageRemarks());
            item.setRemarks(itemRequest.getRemarks());
            newItems.add(item);
        }

        confirmation.getItems().clear();
        confirmation.getItems().addAll(newItems);
    }

    private double nonNegative(Double value, String field) {
        if (value == null || value < 0) {
            throw new RuntimeException(field + " is required and cannot be negative");
        }
        return value;
    }

    private LoadingConfirmationResponseDTO mapResponse(LoadingConfirmation confirmation) {
        LoadingConfirmationResponseDTO dto = new LoadingConfirmationResponseDTO();
        Trip trip = confirmation.getTrip();
        Shipment shipment = trip.getShipment();

        dto.setId(confirmation.getId());
        dto.setTripId(trip.getId());
        dto.setTripNo(trip.getTripNo());
        dto.setShipmentId(shipment == null ? null : shipment.getId());
        dto.setShipmentNo(shipment == null ? null : shipment.getShipmentNo());
        dto.setVehicleNumber(trip.getVehicle() == null ? null : trip.getVehicle().getVehicleNumber());
        dto.setDriverName(trip.getDriverName());
        dto.setDriverMobile(trip.getDriverMobile());
        dto.setStatus(confirmation.getStatus());
        dto.setLoadingStartTime(confirmation.getLoadingStartTime());
        dto.setLoadingEndTime(confirmation.getLoadingEndTime());
        dto.setPlannedShipmentWeight(shipment == null ? null : shipment.getTotalWeight());
        dto.setActualGrossWeight(confirmation.getActualGrossWeight());
        dto.setTareWeight(confirmation.getTareWeight());
        dto.setActualNetWeight(confirmation.getActualNetWeight());
        dto.setPackageCount(confirmation.getPackageCount());
        dto.setPalletCount(confirmation.getPalletCount());
        dto.setSealNumber(confirmation.getSealNumber());
        dto.setRemarks(confirmation.getRemarks());
        dto.setConfirmedBy(confirmation.getConfirmedBy());
        dto.setConfirmedAt(confirmation.getConfirmedAt());
        dto.setSupervisorRemarks(confirmation.getSupervisorRemarks());
        dto.setReviewedBy(confirmation.getReviewedBy());
        dto.setReviewedAt(confirmation.getReviewedAt());
        dto.setCreatedAt(confirmation.getCreatedAt());
        dto.setUpdatedAt(confirmation.getUpdatedAt());

        List<LoadingConfirmationItemResponseDTO> itemDtos = new ArrayList<>();
        if (confirmation.getItems() != null) {
            for (LoadingConfirmationItem item : confirmation.getItems()) {
                LoadingConfirmationItemResponseDTO itemDto =
                        new LoadingConfirmationItemResponseDTO();
                itemDto.setId(item.getId());
                itemDto.setSalesOrderLineId(item.getSalesOrderLine() == null
                        ? null : item.getSalesOrderLine().getId());
                itemDto.setSalesOrderId(item.getSalesOrderId());
                itemDto.setOrderNumber(item.getOrderNumber());
                itemDto.setItemNumber(item.getItemNumber());
                itemDto.setItemDescription(item.getItemDescription());
                itemDto.setUom(item.getUom());
                itemDto.setOrderedQuantity(item.getOrderedQuantity());
                itemDto.setLoadedQuantity(item.getLoadedQuantity());
                itemDto.setDamagedQuantity(item.getDamagedQuantity());
                itemDto.setRejectedQuantity(item.getRejectedQuantity());
                itemDto.setShortQuantity(item.getShortQuantity());
                itemDto.setDamageRemarks(item.getDamageRemarks());
                itemDto.setRemarks(item.getRemarks());
                itemDtos.add(itemDto);
            }
        }
        dto.setItems(itemDtos);
        return dto;
    }
}
