package com.tms.transportationManagementSystem.integration.jde.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class JdeItemMasterApiResponse {

    @JsonProperty("F4101 Count")
    private Integer count;

    @JsonProperty("DREQ_Item_Master")
    private List<JdeItemResponseDTO> itemList;
}