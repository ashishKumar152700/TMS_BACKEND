package com.tms.transportationManagementSystem.integration.jde.client;

import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeCustomerApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class JdeCustomerClient {

    private final RestClient restClient;

    private static final String CUSTOMER_URL =
            "http://192.168.0.36:7018/jderest/orchestrator/ORCH_Customer_Data";

    private static final String USERNAME =
            "TRAINEE04";

    private static final String PASSWORD =
            "Local@4";

    public JdeCustomerApiResponse getCustomers() {

        return restClient.post()
                .uri(CUSTOMER_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .body("{}")
                .retrieve()
                .body(JdeCustomerApiResponse.class);
    }
}