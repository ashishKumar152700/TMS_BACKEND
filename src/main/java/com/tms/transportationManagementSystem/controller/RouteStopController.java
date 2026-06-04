package com.tms.transportationManagementSystem.controller;


import com.tms.transportationManagementSystem.dto.RequestDTO.AddMultipleRouteStopsRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.AddRouteStopRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.RouteStopResponseDTO;
import com.tms.transportationManagementSystem.service.RouteStopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/route-stops")
@RequiredArgsConstructor
public class RouteStopController {

    private final RouteStopService routeStopService;

    @PostMapping
    public RouteStopResponseDTO addRouteStop(
            @RequestBody AddRouteStopRequestDTO requestDTO) {

        return routeStopService.addRouteStop(requestDTO);
    }

    @GetMapping
    public List<RouteStopResponseDTO> getAllRouteStops() {

        return routeStopService.getAllRouteStops();
    }

    @GetMapping("/{id}")
    public RouteStopResponseDTO getRouteStopById(
            @PathVariable Long id) {

        return routeStopService.getRouteStopById(id);
    }

    @PostMapping("/{routeId}")
    public List<RouteStopResponseDTO> addRouteStops(

            @PathVariable Long routeId,

            @RequestBody
            List<AddMultipleRouteStopsRequestDTO>
                    requestDTOList
    ) {

        return routeStopService.addRouteStops(
                routeId,
                requestDTOList
        );
    }

    @PutMapping("/{id}")
    public RouteStopResponseDTO updateRouteStop(
            @PathVariable Long id,
            @RequestBody AddRouteStopRequestDTO requestDTO) {

        return routeStopService.updateRouteStop(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteRouteStop(
            @PathVariable Long id) {

        routeStopService.deleteRouteStop(id);

        return "Route stop deleted successfully";
    }
}