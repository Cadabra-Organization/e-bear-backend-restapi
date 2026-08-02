package com.example.ebearrestapi.gateway.toss;

import com.example.ebearrestapi.dto.request.PaymentConfirmDto;
import com.example.ebearrestapi.dto.request.TossCancelDto;
import com.example.ebearrestapi.dto.request.TossConfirmDto;
import com.example.ebearrestapi.etc.PgProvider;
import com.example.ebearrestapi.exception.PaymentException;
import com.example.ebearrestapi.gateway.PaymentGateway;
import com.example.ebearrestapi.gateway.dto.PaymentResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TossPaymentGateway implements PaymentGateway {

    private final TossPaymentClient tossPaymentClient;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(PgProvider pgProvider) {
        return pgProvider == PgProvider.TOSS;
    }

    /**
     * 결제 승인 요청
     */
    @Override
    public PaymentResponseDto confirm(PaymentConfirmDto confirmDto) {
        // 공통 DTO를 토스 전용 DTO로 매핑
        TossConfirmDto tossConfirmDto = TossConfirmDto.builder()
                .paymentKey(confirmDto.getPaymentKey())
                .orderId(confirmDto.getOrderId())
                .amount(confirmDto.getAmount())
                .build();

        String responseBody;
        try {
            responseBody = tossPaymentClient.confirm(tossConfirmDto);
        } catch (RetryableException e) {
            // 응답 자체를 못 받은 경우(커넥션/타임아웃)
            log.error("토스 승인 에러 응답: {}", e.contentUTF8());
            throw new PaymentException("NETWORK_ERROR", "결제 서버와 통신할 수 없습니다.");
        } catch (FeignException e) {
            // 토스가 실제로 에러 응답을 내려준 경우 (ex. 승인 거절 등)
            log.error("토스 승인 에러 응답: {}", e.contentUTF8());
            throw new PaymentException("PG_DECLINED", e.contentUTF8());
        }

        try{
            JsonNode body = objectMapper.readTree(responseBody);

            return PaymentResponseDto.builder()
                    .isSuccess("DONE".equals(body.get("status").asText()))
                    .transactionId(body.get("paymentKey").asText())
                    .rawResponse(responseBody)
                    .build();
        } catch (JsonProcessingException e) {
            log.error("토스 응답 파싱 실패", e);
            throw new PaymentException("PG_RESPONSE_PARSE_ERROR", "결제 서버 응답을 해석할 수 없습니다");
        }
    }

    /**
     * 결제 취소 요청
     */
    @Override
    public void cancel(String paymentKey, String reason) {
        log.info("토스 결제 취소 요청: paymentKey={}", paymentKey);

        TossCancelDto cancelDto = TossCancelDto.builder().cancelReason(reason).build();

        try {
            tossPaymentClient.cancel(paymentKey, cancelDto);
        } catch (FeignException e) {
            log.error("토스 결제 취소 실패 : paymentKey={}, reason={}", paymentKey, reason, e);
            throw new PaymentException("PG_CANCEL_FAILED", "PG사 결제 취소에 실패했습니다.");
        }
    }

}
