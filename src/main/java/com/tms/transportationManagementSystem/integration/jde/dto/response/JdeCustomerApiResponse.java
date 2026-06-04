package com.tms.transportationManagementSystem.integration.jde.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class JdeCustomerApiResponse {

    @JsonProperty("V560999P Count")
    private Integer count;

    @JsonProperty("DREQ_Customer_Data")
    private List<JdeCustomerResponseDTO> customerData;
}