package com.example.ebearrestapi.gateway.toss;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TossFeignConfig {

    @Value("${toss.api.secret-key}")
    private String secretKey;

    @Bean
    public RequestInterceptor tossAuthInterceptor() {
        return requestTemplate -> {
            String encodedAuthKey = Base64.getEncoder()
                    .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
            requestTemplate.header("Authorization", "Basic " + encodedAuthKey);
            requestTemplate.header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        };
    }
}
