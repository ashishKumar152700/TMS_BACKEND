package com.tms.transportationManagementSystem.integration.jde.client;

import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeSalesOrderApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "jde-sales-order",
        url = "${jde.base-url}"
)
public interface JdeSalesOrderClient {

    @GetMapping("/salesOrders")
    JdeSalesOrderApiResponse getSalesOrders();
}