package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.CreateShipmentBidDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentBidDTO;
import com.tms.transportationManagementSystem.entity.Shipment;
import com.tms.transportationManagementSystem.entity.ShipmentBid;
import com.tms.transportationManagementSystem.entity.Transporter;
import com.tms.transportationManagementSystem.repository.ShipmentBidRepository;
import com.tms.transportationManagementSystem.repository.ShipmentRepository;
import com.tms.transportationManagementSystem.repository.TransporterRepository;
import com.tms.transportationManagementSystem.service.ShipmentBidService;
import enums.ShipmentBidStatus;
import enums.ShipmentStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShipmentBidServiceImpl
        implements ShipmentBidService {

    private final ShipmentBidRepository
            shipmentBidRepository;

    private final ShipmentRepository
            shipmentRepository;

    private final TransporterRepository
            transporterRepository;

    public ShipmentBidServiceImpl(
            ShipmentBidRepository shipmentBidRepository,
            ShipmentRepository shipmentRepository,
            TransporterRepository transporterRepository
    ) {
        this.shipmentBidRepository =
                shipmentBidRepository;

        this.shipmentRepository =
                shipmentRepository;

        this.transporterRepository =
                transporterRepository;
    }

    @Override
    public ShipmentBidDTO createBid(
            CreateShipmentBidDTO dto
    ) {

        Shipment shipment =
                shipmentRepository.findById(
                                dto.getShipmentId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Shipment not found"
                                )
                        );

        Transporter transporter =
                transporterRepository.findById(
                                dto.getTransporterId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transporter not found"
                                )
                        );

        ShipmentBid shipmentBid =
                new ShipmentBid();

        shipmentBid.setShipmentId(
                shipment.getId()
        );

        shipmentBid.setTransporterId(
                transporter.getId()
        );

        shipmentBid.setBidAmount(
                dto.getBidAmount()
        );

        shipmentBid.setRemarks(
                dto.getRemarks()
        );

        shipmentBid.setStatus(
                ShipmentBidStatus.PENDING
        );

        shipmentBid.setCreatedAt(
                LocalDateTime.now()
        );

        ShipmentBid savedBid =
                shipmentBidRepository.save(
                        shipmentBid
                );

        return mapToDTO(
                savedBid,
                shipment,
                transporter
        );
    }

    @Override
    public List<ShipmentBidDTO>
    getBidsByShipment(Long shipmentId) {

        List<ShipmentBid> bids =
                shipmentBidRepository
                        .findByShipmentId(
                                shipmentId
                        );

        return bids.stream()
                .map(bid -> {

                    Shipment shipment =
                            shipmentRepository
                                    .findById(
                                            bid.getShipmentId()
                                    )
                                    .orElseThrow();

                    Transporter transporter =
                            transporterRepository
                                    .findById(
                                            bid.getTransporterId()
                                    )
                                    .orElseThrow();

                    return mapToDTO(
                            bid,
                            shipment,
                            transporter
                    );
                })
                .toList();
    }

    @Override
    public ShipmentBidDTO acceptBid(
            Long bidId
    ) {

        ShipmentBid acceptedBid =
                shipmentBidRepository
                        .findById(bidId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Bid not found"
                                )
                        );

        // ACCEPT SELECTED BID
        acceptedBid.setStatus(
                ShipmentBidStatus.ACCEPTED
        );

        shipmentBidRepository.save(
                acceptedBid
        );

        // GET ALL BIDS OF SAME SHIPMENT
        List<ShipmentBid> shipmentBids =
                shipmentBidRepository
                        .findByShipmentId(
                                acceptedBid.getShipmentId()
                        );

        // REJECT OTHER BIDS
        for (ShipmentBid bid : shipmentBids) {

            if (!bid.getId().equals(
                    acceptedBid.getId()
            )) {

                bid.setStatus(
                        ShipmentBidStatus.REJECTED
                );

                shipmentBidRepository.save(
                        bid
                );
            }
        }

        // UPDATE SHIPMENT STATUS
        Shipment shipment =
                shipmentRepository
                        .findById(
                                acceptedBid.getShipmentId()
                        )
                        .orElseThrow();

        shipment.setStatus(
                ShipmentStatus.BID_FINALIZED
        );

        shipmentRepository.save(
                shipment
        );

        Transporter transporter =
                transporterRepository
                        .findById(
                                acceptedBid.getTransporterId()
                        )
                        .orElseThrow();

        return mapToDTO(
                acceptedBid,
                shipment,
                transporter
        );
    }

    private ShipmentBidDTO mapToDTO(

            ShipmentBid bid,

            Shipment shipment,

            Transporter transporter
    ) {

        ShipmentBidDTO dto =
                new ShipmentBidDTO();

        dto.setId(
                bid.getId()
        );

        dto.setShipmentId(
                shipment.getId()
        );

        dto.setShipmentNo(
                shipment.getShipmentNo()
        );

        dto.setTransporterId(
                transporter.getId()
        );

        dto.setTransporterName(
                transporter.getCompanyName()
        );

        dto.setBidAmount(
                bid.getBidAmount()
        );

        dto.setRemarks(
                bid.getRemarks()
        );

        dto.setStatus(
                bid.getStatus().name()
        );

        dto.setCreatedAt(
                bid.getCreatedAt()
        );

        return dto;
    }
}