package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.ResponseDTO.GatePassResponseDTO;
import com.tms.transportationManagementSystem.service.GatePassService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gate-passes")
@RequiredArgsConstructor
public class GatePassController {

    private final GatePassService gatePassService;

    @GetMapping
    public List<GatePassResponseDTO> getAllGatePasses(
            @RequestParam(required = false) String status) {
        return gatePassService.getAllGatePasses(status);
    }

    @PostMapping("/{id}/gate-out")
    public GatePassResponseDTO gateOut(@PathVariable Long id) {
        return gatePassService.gateOut(id);
    }
}