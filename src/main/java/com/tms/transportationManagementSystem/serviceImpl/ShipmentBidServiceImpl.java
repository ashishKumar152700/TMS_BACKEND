package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentBidRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.*;
import com.tms.transportationManagementSystem.entity.*;
import com.tms.transportationManagementSystem.enums.ShipmentBidStatus;
import com.tms.transportationManagementSystem.enums.ShipmentStatus;
import com.tms.transportationManagementSystem.repository.*;
import com.tms.transportationManagementSystem.service.ShipmentBidService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShipmentBidServiceImpl implements ShipmentBidService {

    private final ShipmentBidRepository shipmentBidRepository;
    private final ShipmentRepository shipmentRepository;
    private final TransporterRepository transporterRepository;
    private final UserRepository userRepository;
    private final TransporterShipmentBidTrackerRepository trackerRepository;
    private final SalesOrderLookup salesOrderLookup;


    @Override
    public ShipmentBidResponseDTO rejectBid(Long bidId) {

        ShipmentBid bid = shipmentBidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        if (bid.getBidStatus() == ShipmentBidStatus.ACCEPTED) {
            throw new RuntimeException("Accepted bid cannot be rejected.");
        }

        bid.setBidStatus(ShipmentBidStatus.REJECTED);

        shipmentBidRepository.save(bid);

        return map(bid);
    }


    @Override
    public List<AppliedBidResponseDTO> getMyAppliedBids() {

        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long jdeAddressNumber = currentUser.getJdeAddressNumber();

        Transporter transporter = transporterRepository
                .findByAddressNumber(jdeAddressNumber)
                .orElseThrow(() -> new RuntimeException("Transporter not found"));

        List<ShipmentBid> bids = shipmentBidRepository
                .findByTransporterIdOrderByBidDateDesc(transporter.getId());

        // Saare bids ke shipments ke orders ek saath load (N+1 se bachne ke liye)
        Map<Long, List<JdeSalesOrder>> ordersByShipmentId =
                salesOrderLookup.ordersByShipmentId(
                        bids.stream()
                                .map(bid -> bid.getShipment().getId())
                                .toList()
                );

        return bids.stream()
                .map(bid -> {

                    Shipment shipment = bid.getShipment();

                    List<ShipmentOrderResponseDTO> orderList =
                            toOrderDTOs(ordersByShipmentId, shipment.getId());

                    // Bid Status Display
                    String bidStatus;
                    switch (bid.getBidStatus()) {
                        case PENDING:
                            bidStatus = "Waiting for Approval";
                            break;

                        case ACCEPTED:
                            bidStatus = "Accepted";
                            break;

                        case REJECTED:
                            bidStatus = "Rejected";
                            break;

                        default:
                            bidStatus = bid.getBidStatus().name();
                    }

                    return AppliedBidResponseDTO.builder()
                            .bidId(bid.getId())
                            .shipmentId(shipment.getId())
                            .shipmentNo(shipment.getShipmentNo())

                            .bidAmount(bid.getBidAmount())
                            .deliveryDays(bid.getDeliveryDays())
                            .plannedPickupDate(bid.getPlannedPickupDate())
                            .remarks(bid.getRemarks())
                            .bidDate(bid.getBidDate())
                            .bidStatus(bidStatus)

                            .expectedPrice(shipment.getExpectedPrice())
                            .vehicleType(shipment.getSelectedVehicleType())
                            .totalWeight(shipment.getTotalWeight())
                            .totalVolume(shipment.getTotalVolume())
                            .shipmentStatus(shipment.getStatus().name())

                            .orders(orderList)

                            .build();

                })
                .toList();
    }

    @Override
    public List<ShipmentBidManagementResponseDTO> getAllShipmentBidsForAdmin() {

        List<Shipment> shipments = shipmentRepository.findByStatusIn(
                List.of(
                        ShipmentStatus.BIDDING_OPEN,
                        ShipmentStatus.BID_FINALIZED
                )
        );

        Map<Long, List<JdeSalesOrder>> ordersByShipmentId =
                salesOrderLookup.ordersByShipmentId(
                        shipments.stream()
                                .map(Shipment::getId)
                                .toList()
                );

        return shipments.stream()
                .map(shipment -> {

                    // Orders
                    List<ShipmentOrderResponseDTO> orderDTOs =
                            toOrderDTOs(ordersByShipmentId, shipment.getId());

                    // Bids
                    List<ShipmentBid> bids;

                    if (shipment.getStatus() == ShipmentStatus.BID_FINALIZED) {
                        // Sirf accepted bid dikhao
                        bids = shipmentBidRepository
                                .findByShipmentIdAndBidStatusOrderByBidAmountAsc(
                                        shipment.getId(),
                                        ShipmentBidStatus.ACCEPTED
                                );
                    } else {
                        // Bidding open hai to sab bids dikhao
                        bids = shipmentBidRepository
                                .findByShipmentIdOrderByBidAmountAsc(shipment.getId());
                    }

                    List<ShipmentBidAdminResponseDTO> bidDTOs =
                            bids.stream()
                                    .map(bid -> ShipmentBidAdminResponseDTO.builder()
                                            .bidId(bid.getId())
                                            .transporterId(bid.getTransporter().getId())
                                            .transporterName(bid.getTransporter().getCompanyName())
                                            .bidAmount(bid.getBidAmount())
                                            .deliveryDays(bid.getDeliveryDays())
                                            .plannedPickupDate(bid.getPlannedPickupDate())
                                            .remarks(bid.getRemarks())
                                            .bidDate(bid.getBidDate())
                                            .bidStatus(bid.getBidStatus().name())
                                            .build())
                                    .toList();

                    return ShipmentBidManagementResponseDTO.builder()
                            .shipmentId(shipment.getId())
                            .shipmentNo(shipment.getShipmentNo())
                            .vehicleType(shipment.getSelectedVehicleType())
                            .totalWeight(shipment.getTotalWeight())
                            .totalVolume(shipment.getTotalVolume())
                            .expectedPrice(shipment.getExpectedPrice())
                            .plannedPickupDate(shipment.getPlannedPickupDate())
                            .shipmentStatus(shipment.getStatus().name())
                            .orders(orderDTOs)
                            .bids(bidDTOs)
                            .build();

                })
                .toList();
    }

    @Override
    public ShipmentBidResponseDTO placeBid(
            ShipmentBidRequestDTO request,
            Long userId) {

        // 1. Shipment
        Shipment shipment = shipmentRepository.findById(request.getShipmentId())
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        // 2. User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Role check
        boolean isAllowed = user.getRoles().stream()
                .anyMatch(r -> r.getRoleName().equalsIgnoreCase("TRANSPORTER")
                        || r.getRoleName().equalsIgnoreCase("ADMIN"));

        if (!isAllowed) {
            throw new RuntimeException("User not allowed to place bid");
        }

        // 4. Transporter mapping via JDE
        Transporter transporter =
                transporterRepository.findByAddressNumber(user.getJdeAddressNumber())
                        .orElseThrow(() -> new RuntimeException("Transporter not mapped with JDE id"));

        // 5. Duplicate check (NOW USING TRACKER TABLE)
        if (trackerRepository.existsByShipmentIdAndTransporterId(
                shipment.getId(), transporter.getId())) {

            throw new RuntimeException("Bid already submitted.");
        }

        // 6. Create Bid
        ShipmentBid bid = new ShipmentBid();

        bid.setShipment(shipment);
        bid.setTransporter(transporter);
        bid.setUser(user);
        bid.setBidAmount(request.getBidAmount());
        bid.setDeliveryDays(request.getDeliveryDays());
        bid.setPlannedPickupDate(request.getPlannedPickupDate());
        bid.setRemarks(request.getRemarks());
        bid.setBidDate(LocalDateTime.now());
        bid.setBidStatus(ShipmentBidStatus.PENDING);

        ShipmentBid saved = shipmentBidRepository.save(bid);

        TransporterShipmentBidTracker tracker = new TransporterShipmentBidTracker();

        tracker.setShipmentId(shipment.getId());
        tracker.setTransporterId(transporter.getId());
        tracker.setBidTime(LocalDateTime.now());

        trackerRepository.save(tracker);

        return map(saved);
    }

    @Override
    public List<ShipmentBidResponseDTO> getMyBids(Long transporterId) {

        return shipmentBidRepository.findByTransporterId(transporterId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public List<ShipmentBidResponseDTO> getShipmentBids(Long shipmentId) {

        return shipmentBidRepository.findByShipmentId(shipmentId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public ShipmentBidResponseDTO acceptBid(Long bidId) {

        ShipmentBid acceptedBid = shipmentBidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        // Validation
        if (acceptedBid.getBidStatus() == ShipmentBidStatus.ACCEPTED) {
            throw new RuntimeException("Bid already accepted.");
        }

        if (acceptedBid.getBidStatus() == ShipmentBidStatus.REJECTED) {
            throw new RuntimeException("Rejected bid cannot be accepted.");
        }

        Shipment shipment = acceptedBid.getShipment();

        acceptedBid.setBidStatus(ShipmentBidStatus.ACCEPTED);

        List<ShipmentBid> allBids =
                shipmentBidRepository.findByShipmentId(shipment.getId());

        for (ShipmentBid bid : allBids) {

            if (!bid.getId().equals(acceptedBid.getId())) {

                // Accepted bid ko chhodkar sab reject
                bid.setBidStatus(ShipmentBidStatus.REJECTED);
            }
        }

        shipmentBidRepository.saveAll(allBids);

        shipment.setStatus(ShipmentStatus.BID_FINALIZED);
        shipmentRepository.save(shipment);

        return map(acceptedBid);
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

    private ShipmentBidResponseDTO map(ShipmentBid bid) {

        ShipmentBidResponseDTO dto = new ShipmentBidResponseDTO();

        dto.setBidId(bid.getId());
        dto.setShipmentId(bid.getShipment().getId());

        dto.setTransporterId(bid.getTransporter().getId());
        dto.setTransporterName(bid.getTransporter().getCompanyName());

        dto.setBidAmount(bid.getBidAmount());
        dto.setDeliveryDays(bid.getDeliveryDays());
        dto.setPlannedPickupDate(bid.getPlannedPickupDate());

        dto.setRemarks(bid.getRemarks());

        dto.setBidStatus(bid.getBidStatus().name());

        return dto;
    }

}