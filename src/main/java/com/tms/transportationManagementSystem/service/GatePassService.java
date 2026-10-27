package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.ResponseDTO.GatePassResponseDTO;
import java.util.List;

public interface GatePassService {

    List<GatePassResponseDTO> getAllGatePasses();

    GatePassResponseDTO gateOut(Long gatePassId);

}