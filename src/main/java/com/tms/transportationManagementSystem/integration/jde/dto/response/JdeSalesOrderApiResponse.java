package com.tms.transportationManagementSystem.integration.jde.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeSalesOrderResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class JdeSalesOrderApiResponse {

    private Integer count;

    private List<JdeSalesOrderResponseDTO> orders;

    @JsonProperty("jde__status")
    private String status;

    @JsonProperty("jde__startTimestamp")
    private String startTimestamp;

    @JsonProperty("jde__endTimestamp")
    private String endTimestamp;
}