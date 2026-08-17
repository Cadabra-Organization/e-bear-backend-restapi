package com.example.ebearrestapi.gateway.toss;

import com.example.ebearrestapi.dto.request.PaymentConfirmDto;
import com.example.ebearrestapi.dto.request.TossCancelDto;
import com.example.ebearrestapi.dto.request.TossConfirmDto;
import com.example.ebearrestapi.etc.PgProvider;
import com.example.ebearrestapi.gateway.PaymentGateway;
import com.example.ebearrestapi.gateway.dto.PayoutResponseDto;
import com.example.ebearrestapi.gateway.dto.PaymentResponseDto;
import com.example.ebearrestapi.gateway.dto.PgSettlementDto;
import com.fasterxml.jackson.databind.JsonNode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TossPaymentGateway implements PaymentGateway {

    private final TossPaymentClient tossPaymentClient;

    @Override
    public boolean supports(PgProvider pgProvider) {
        return pgProvider == PgProvider.TOSS;
    }

    @Override
    public PaymentResponseDto confirm(PaymentConfirmDto confirmDto) {
        TossConfirmDto tossConfirmDto = TossConfirmDto.builder()
                .paymentKey(confirmDto.getPaymentKey())
                .orderId(confirmDto.getOrderId())
                .amount(confirmDto.getAmount())
                .build();

        try {
            JsonNode body = tossPaymentClient.confirm(tossConfirmDto);

            return PaymentResponseDto.builder()
                    .isSuccess("DONE".equals(body.path("status").asText()))
                    .transactionId(body.path("paymentKey").asText())
                    .rawResponse(body.toString())
                    .build();
        } catch (FeignException e) {
            log.error("Toss Feign confirm error: status={}, content={}", e.status(), e.contentUTF8());
            return PaymentResponseDto.builder()
                    .isSuccess(false)
                    .errorMessage(e.contentUTF8())
                    .build();
        } catch (Exception e) {
            log.error("Toss confirm unexpected error", e);
            return PaymentResponseDto.builder()
                    .isSuccess(false)
                    .errorMessage("Payment gateway error")
                    .build();
        }
    }

    @Override
    public void cancel(String paymentKey, String reason) {
        TossCancelDto cancelDto = TossCancelDto.builder().cancelReason(reason).build();
        try {
            tossPaymentClient.cancel(paymentKey, cancelDto);
        } catch (Exception e) {
            log.error("Toss Feign cancel failed: paymentKey={}, reason={}", paymentKey, reason, e);
        }
    }

    @Override
    public List<PgSettlementDto> getSettlementHistory(LocalDate startDate, LocalDate endDate) {
        try {
            JsonNode rootNode = tossPaymentClient.getSettlements(
                    startDate.toString(),
                    endDate.toString(),
                    "PAID",
                    0,
                    1000
            );

            List<PgSettlementDto> resultList = new ArrayList<>();
            if (rootNode != null && rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    LocalDateTime approvedAt = null;
                    if (node.hasNonNull("approvedAt")) {
                        approvedAt = OffsetDateTime.parse(node.get("approvedAt").asText()).toLocalDateTime();
                    }

                    LocalDate soldDate = null;
                    if (node.hasNonNull("soldDate")) {
                        soldDate = LocalDate.parse(node.get("soldDate").asText());
                    }

                    LocalDate paidOutDate = null;
                    if (node.hasNonNull("paidOutDate")) {
                        paidOutDate = LocalDate.parse(node.get("paidOutDate").asText());
                    }

                    resultList.add(PgSettlementDto.builder()
                            .paymentKey(node.path("paymentKey").asText(null))
                            .orderId(node.path("orderId").asText(null))
                            .amount(node.path("amount").asInt(0))
                            .fee(node.path("fee").asInt(0))
                            .payOutAmount(node.path("payOutAmount").asInt(0))
                            .soldDate(soldDate)
                            .paidOutDate(paidOutDate)
                            .approvedAt(approvedAt)
                            .build());
                }
            }
            return resultList;
        } catch (Exception e) {
            log.warn("Toss Feign settlement API call failed or simulated: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public PayoutResponseDto requestPayout(String bankName, String accountNumber, String accountHolder, int amount) {
        String payoutId = "PO_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        log.info("Executing Toss Payout: payoutId={}, receiver={}({}), amount={}",
                payoutId, bankName, accountNumber, amount);

        return PayoutResponseDto.builder()
                .isSuccess(true)
                .payoutId(payoutId)
                .amount(amount)
                .receiverBank(bankName)
                .receiverAccount(accountNumber)
                .paidAt(LocalDateTime.now())
                .build();
    }
}
