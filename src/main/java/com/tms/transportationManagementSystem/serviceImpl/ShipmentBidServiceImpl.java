package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentBidRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentBidResponseDTO;
import com.tms.transportationManagementSystem.entity.Shipment;
import com.tms.transportationManagementSystem.entity.ShipmentBid;
import com.tms.transportationManagementSystem.entity.Transporter;
import com.tms.transportationManagementSystem.enums.ShipmentBidStatus;
import com.tms.transportationManagementSystem.enums.ShipmentStatus;
import com.tms.transportationManagementSystem.repository.ShipmentBidRepository;
import com.tms.transportationManagementSystem.repository.ShipmentRepository;
import com.tms.transportationManagementSystem.repository.TransporterRepository;
import com.tms.transportationManagementSystem.service.ShipmentBidService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentBidServiceImpl
        implements ShipmentBidService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentBidRepository shipmentBidRepository;
    private final TransporterRepository transporterRepository;

    @Override
    public String openBidding(Long shipmentId) {

        Shipment shipment =
                shipmentRepository.findById(shipmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Shipment not found"));

        shipment.setStatus(
                ShipmentStatus.BIDDING_OPEN);

        shipmentRepository.save(shipment);

        return "Bidding opened successfully";
    }

    @Override
    public ShipmentBidResponseDTO placeBid(
            ShipmentBidRequestDTO request) {

        Shipment shipment =
                shipmentRepository.findById(
                                request.getShipmentId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Shipment not found"));

        Transporter transporter =
                transporterRepository.findById(
                                request.getTransporterId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transporter not found"));

        ShipmentBid bid = new ShipmentBid();

        bid.setShipment(shipment);
        bid.setTransporter(transporter);
        bid.setBidAmount(request.getBidAmount());
        bid.setVehicleCount(request.getVehicleCount());
        bid.setRemarks(request.getRemarks());
        bid.setBidDate(LocalDateTime.now());
        bid.setBidStatus(ShipmentBidStatus.PENDING);
        ShipmentBid savedBid =
                shipmentBidRepository.save(bid);

        return mapToResponse(savedBid);
    }

    @Override
    public List<ShipmentBidResponseDTO>
    getShipmentBids(Long shipmentId) {

        return shipmentBidRepository
                .findByShipmentId(shipmentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public String acceptBid(Long bidId) {

        ShipmentBid selectedBid =
                shipmentBidRepository.findById(bidId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Bid not found"));

        Shipment shipment =
                selectedBid.getShipment();

        List<ShipmentBid> allBids =
                shipmentBidRepository
                        .findByShipmentId(
                                shipment.getId());

        for (ShipmentBid bid : allBids) {

            bid.setBidStatus(ShipmentBidStatus.PENDING);

            shipmentBidRepository.save(bid);
        }

        selectedBid.setBidStatus(
                ShipmentBidStatus.ACCEPTED);

        shipmentBidRepository.save(selectedBid);

        shipment.setStatus(
                ShipmentStatus.BID_FINALIZED);

        shipmentRepository.save(shipment);

        return "Bid accepted successfully";
    }

    private ShipmentBidResponseDTO
    mapToResponse(ShipmentBid bid) {

        return ShipmentBidResponseDTO.builder()
                .bidId(bid.getId())
                .shipmentId(
                        bid.getShipment().getId())
                .transporterId(
                        bid.getTransporter().getId())
                .transporterName(
                        bid.getTransporter()
                                .getCompanyName())
                .bidAmount(
                        bid.getBidAmount())
                .vehicleCount(
                        bid.getVehicleCount())
                .remarks(
                        bid.getRemarks())

                .build();
    }
}