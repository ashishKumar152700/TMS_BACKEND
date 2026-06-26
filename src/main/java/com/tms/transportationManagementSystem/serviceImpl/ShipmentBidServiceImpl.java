package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentBidRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentBidResponseDTO;
import com.tms.transportationManagementSystem.entity.*;
import com.tms.transportationManagementSystem.enums.ShipmentBidStatus;
import com.tms.transportationManagementSystem.enums.ShipmentStatus;
import com.tms.transportationManagementSystem.repository.*;
import com.tms.transportationManagementSystem.service.ShipmentBidService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentBidServiceImpl implements ShipmentBidService {

    private final ShipmentBidRepository shipmentBidRepository;
    private final ShipmentRepository shipmentRepository;
    private final TransporterRepository transporterRepository;
    private final UserRepository userRepository;
    private final TransporterShipmentBidTrackerRepository trackerRepository;

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

        Shipment shipment = acceptedBid.getShipment();

        acceptedBid.setBidStatus(ShipmentBidStatus.ACCEPTED);

        List<ShipmentBid> allBids =
                shipmentBidRepository.findByShipmentId(shipment.getId());

        for (ShipmentBid bid : allBids) {

            if (!bid.getId().equals(acceptedBid.getId())) {

                bid.setBidStatus(ShipmentBidStatus.REJECTED);

            }

        }

        shipmentBidRepository.saveAll(allBids);

        shipment.setStatus(ShipmentStatus.BID_FINALIZED);
        shipmentRepository.save(shipment);

        return map(acceptedBid);
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