package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddRouteRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.RouteResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TransporterRouteDetailsDTO;
import com.tms.transportationManagementSystem.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping("/transporter/{transporterId}")
    public List<TransporterRouteDetailsDTO>
    getRoutesByTransporter(
            @PathVariable Long transporterId
    ) {

        return routeService.getRoutesByTransporter(
                transporterId
        );
    }

    @PostMapping
    public RouteResponseDTO addRoute(
            @RequestBody AddRouteRequestDTO requestDTO) {

        return routeService.addRoute(requestDTO);
    }

    @GetMapping
    public List<RouteResponseDTO> getAllRoutes() {

        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public RouteResponseDTO getRouteById(
            @PathVariable Long id) {

        return routeService.getRouteById(id);
    }

    @PutMapping("/{id}")
    public RouteResponseDTO updateRoute(
            @PathVariable Long id,
            @RequestBody AddRouteRequestDTO requestDTO) {

        return routeService.updateRoute(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteRoute(@PathVariable Long id) {

        routeService.deleteRoute(id);

        return "Route deleted successfully";
    }

    @GetMapping("/suggestions")
    public List<RouteResponseDTO> getSuggestedRoutes(
            @RequestParam String pickup,
            @RequestParam String delivery
    ) {

        return routeService.getSuggestedRoutes(
                pickup,
                delivery
        );
    }
}