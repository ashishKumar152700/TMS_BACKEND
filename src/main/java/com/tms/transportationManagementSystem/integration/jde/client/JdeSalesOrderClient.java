package com.tms.transportationManagementSystem.integration.jde.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeSalesOrderApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class JdeSalesOrderClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private final String username = "TRAINEE04";
    private final String password = "Local@4";

    private final String salesOrderUrl =
            "http://192.168.0.36:7018/jderest/orchestrator/ORCHSales_Order";

    public JdeSalesOrderApiResponse getSalesOrders() {

        try {

            String basicAuth = Base64.getEncoder()
                    .encodeToString(
                            (username + ":" + password)
                                    .getBytes(StandardCharsets.UTF_8)
                    );

            String response = restClient.post()
                    .uri(salesOrderUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{}")
                    .retrieve()
                    .body(String.class);

            log.debug("SALES ORDER RESPONSE: {}", response);

            return objectMapper.readValue(
                    response,
                    JdeSalesOrderApiResponse.class
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch sales orders", e);
        }
    }
}