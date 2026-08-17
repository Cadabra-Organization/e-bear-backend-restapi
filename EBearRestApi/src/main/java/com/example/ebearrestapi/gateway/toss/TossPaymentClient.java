package com.example.ebearrestapi.gateway.toss;

import com.example.ebearrestapi.dto.request.TossCancelDto;
import com.example.ebearrestapi.dto.request.TossConfirmDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "tossPaymentClient",
        url = "https://${toss.api.base-url}",
        configuration = TossFeignConfig.class
)
public interface TossPaymentClient {

    @PostMapping(value = "/v1/payments/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    JsonNode confirm(@RequestBody TossConfirmDto tossConfirmDto);

    @PostMapping(value = "/v1/payments/{paymentKey}/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    void cancel(@PathVariable("paymentKey") String paymentKey, @RequestBody TossCancelDto cancelDto);

    @GetMapping(value = "/v1/settlements", produces = MediaType.APPLICATION_JSON_VALUE)
    JsonNode getSettlements(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "dateType", defaultValue = "PAID") String dateType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "1000") int size
    );
}
