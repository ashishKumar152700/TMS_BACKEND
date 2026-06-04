package com.tms.transportationManagementSystem.controller;


import com.tms.transportationManagementSystem.dto.RequestDTO.AddTransporterRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TransporterResponseDTO;
import com.tms.transportationManagementSystem.service.TransporterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transporters")
@RequiredArgsConstructor
public class TransporterController {

    private final TransporterService transporterService;

    @PostMapping
    public TransporterResponseDTO addTransporter(
            @RequestBody AddTransporterRequestDTO requestDTO) {

        return transporterService.addTransporter(requestDTO);
    }

    @GetMapping
    public List<TransporterResponseDTO> getAllTransporters() {

        return transporterService.getAllTransporters();
    }

    @GetMapping("/{id}")
    public TransporterResponseDTO getTransporterById(
            @PathVariable Long id) {

        return transporterService.getTransporterById(id);
    }

    @PutMapping("/{id}")
    public TransporterResponseDTO updateTransporter(
            @PathVariable Long id,
            @RequestBody AddTransporterRequestDTO requestDTO) {

        return transporterService.updateTransporter(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteTransporter(@PathVariable Long id) {

        transporterService.deleteTransporter(id);

        return "Transporter deleted successfully";
    }
}