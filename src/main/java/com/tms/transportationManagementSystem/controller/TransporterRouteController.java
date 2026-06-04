package com.tms.transportationManagementSystem.controller;


import com.tms.transportationManagementSystem.dto.RequestDTO.AddTransporterRouteRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TransporterRouteResponseDTO;
import com.tms.transportationManagementSystem.service.TransporterRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transporter-routes")
@RequiredArgsConstructor
public class TransporterRouteController {

    private final TransporterRouteService
            transporterRouteService;

    @PostMapping
    public TransporterRouteResponseDTO addMapping(
            @RequestBody
            AddTransporterRouteRequestDTO requestDTO) {

        return transporterRouteService
                .addTransporterRoute(requestDTO);
    }

    @GetMapping
    public List<TransporterRouteResponseDTO> getAllMappings() {

        return transporterRouteService.getAllMappings();
    }

    @GetMapping("/{id}")
    public TransporterRouteResponseDTO getMappingById(
            @PathVariable Long id) {

        return transporterRouteService.getMappingById(id);
    }

    @PutMapping("/{id}")
    public TransporterRouteResponseDTO updateMapping(
            @PathVariable Long id,
            @RequestBody
            AddTransporterRouteRequestDTO requestDTO) {

        return transporterRouteService
                .updateMapping(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteMapping(
            @PathVariable Long id) {

        transporterRouteService.deleteMapping(id);

        return "Mapping deleted successfully";
    }
}