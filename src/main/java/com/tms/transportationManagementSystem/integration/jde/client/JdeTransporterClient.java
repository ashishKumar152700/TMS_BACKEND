package com.tms.transportationManagementSystem.integration.jde.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JdeTransporterClient {

    private final RestClient restClient;

    private final String username = "TRAINEE04";
    private final String password = "local@4";

    private final String transporterUrl =
            "http://192.168.0.36:7018/jderest/v3/orchestrator/ORCH_GetTransportWithVehicles";

    public String getTransporters() {

        String basicAuth = Base64.getEncoder()
                .encodeToString(
                        (username + ":" + password)
                                .getBytes(StandardCharsets.UTF_8)
                );

        return restClient.post()
                .uri(transporterUrl)
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + basicAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{}")
                .retrieve()
                .body(String.class);
    }
}