package com.example.ebearrestapi.gateway.toss;

import com.example.ebearrestapi.dto.request.TossCancelDto;
import com.example.ebearrestapi.dto.request.TossConfirmDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "tossPaymentClient",
        url = "${toss.api.base-url}",
        configuration = TossFeignConfig.class
)
public interface TossPaymentClient {

    @PostMapping(value = "/v1/payments/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    String confirm(@RequestBody TossConfirmDto tossConfirmDto);

    @PostMapping(value = "/v1/payments/{paymentKey}/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    void cancel(@PathVariable("paymentKey") String paymentKey, @RequestBody TossCancelDto cancelDto);
}
