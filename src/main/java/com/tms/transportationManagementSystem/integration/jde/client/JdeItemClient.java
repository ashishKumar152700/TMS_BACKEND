package com.tms.transportationManagementSystem.integration.jde.client;

import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeItemMasterApiResponse;
import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeItemResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@Component
@RequiredArgsConstructor
public class JdeItemClient {

    private final RestTemplate restTemplate;

    private final String itemUrl =
            "http://192.168.0.36:7018/jderest/orchestrator/ORCH_Item_Master";

    private final String username = "TRAINEE04";
    private final String password = "Local@4";

    public JdeItemMasterApiResponse getItems() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity =
                new HttpEntity<>("{}", headers);

        ResponseEntity<JdeItemMasterApiResponse> response =
                restTemplate.exchange(
                        itemUrl,
                        HttpMethod.POST,
                        entity,
                        JdeItemMasterApiResponse.class
                );

        return response.getBody();
    }
}