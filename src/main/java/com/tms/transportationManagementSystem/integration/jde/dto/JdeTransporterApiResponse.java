package com.tms.transportationManagementSystem.integration.jde.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JdeTransporterApiResponse {

    @JsonProperty("DREQ_Transport_Master")
    private List<JdeTransporterResponseDTO> transporters;
}