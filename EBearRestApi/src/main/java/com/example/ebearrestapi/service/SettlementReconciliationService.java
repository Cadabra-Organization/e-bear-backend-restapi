package com.example.ebearrestapi.service;

import com.example.ebearrestapi.dto.response.SettlementReconciliationDto;
import com.example.ebearrestapi.entity.PaymentEntity;
import com.example.ebearrestapi.etc.PgProvider;
import com.example.ebearrestapi.gateway.PaymentGateway;
import com.example.ebearrestapi.gateway.PaymentGatewayRegistry;
import com.example.ebearrestapi.gateway.dto.PgSettlementDto;
import com.example.ebearrestapi.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementReconciliationService {

    private final PaymentGatewayRegistry paymentGatewayRegistry;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public SettlementReconciliationDto reconcileSettlement(PgProvider pgProvider, LocalDate startDate, LocalDate endDate) {
        LocalDate start = (startDate != null) ? startDate : LocalDate.now().minusDays(7);
        LocalDate end = (endDate != null) ? endDate : LocalDate.now();

        PaymentGateway gateway = paymentGatewayRegistry.getGateway(pgProvider);
        List<PgSettlementDto> pgSettlements = gateway.getSettlementHistory(start, end);

        int matchedCount = 0;
        int unmatchedCount = 0;
        long totalPgAmount = 0;
        long totalPgFee = 0;
        long totalPgPayout = 0;

        List<SettlementReconciliationDto.DiscrepancyItemDto> discrepancies = new ArrayList<>();

        for (PgSettlementDto pgDto : pgSettlements) {
            totalPgAmount += pgDto.getAmount();
            totalPgFee += pgDto.getFee();
            totalPgPayout += pgDto.getPayOutAmount();

            Optional<PaymentEntity> paymentOpt = Optional.empty();
            if (pgDto.getPaymentKey() != null) {
                paymentOpt = paymentRepository.findByPaymentKey(pgDto.getPaymentKey());
            }

            if (paymentOpt.isEmpty()) {
                unmatchedCount++;
                discrepancies.add(SettlementReconciliationDto.DiscrepancyItemDto.builder()
                        .orderId(pgDto.getOrderId())
                        .paymentKey(pgDto.getPaymentKey())
                        .dbAmount(null)
                        .pgAmount(pgDto.getAmount())
                        .reason("DB에 결제 내역이 존재하지 않음")
                        .build());
            } else {
                PaymentEntity dbPayment = paymentOpt.get();
                if (!dbPayment.getPaymentAmount().equals(pgDto.getAmount())) {
                    unmatchedCount++;
                    discrepancies.add(SettlementReconciliationDto.DiscrepancyItemDto.builder()
                            .orderId(pgDto.getOrderId())
                            .paymentKey(pgDto.getPaymentKey())
                            .dbAmount(dbPayment.getPaymentAmount())
                            .pgAmount(pgDto.getAmount())
                            .reason("DB 결제 금액과 PG사 결제 금액 불일치")
                            .build());
                } else {
                    matchedCount++;
                }
            }
        }

        return SettlementReconciliationDto.builder()
                .startDate(start)
                .endDate(end)
                .totalPgTransactions(pgSettlements.size())
                .matchedCount(matchedCount)
                .unmatchedCount(unmatchedCount)
                .totalPgAmount(totalPgAmount)
                .totalPgFee(totalPgFee)
                .totalPgPayoutAmount(totalPgPayout)
                .discrepancies(discrepancies)
                .build();
    }
}
