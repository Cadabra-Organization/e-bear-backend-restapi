package com.example.ebearrestapi.gateway.toss;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

//@Configuration
public class TossFeignConfig { //인증 헤더 인터셉터
    @Value("${toss.api.secret-key}")
    private String secretKey;

    @Bean
    public RequestInterceptor tossAuthInterceptor() {
        return requestTemplate -> {
            String encodedAuthKey = Base64.getEncoder()
                    .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
            requestTemplate.header("Authorization", "Basic " + encodedAuthKey);
            requestTemplate.header("content-Type", MediaType.APPLICATION_JSON_VALUE);
        };
    }
}
