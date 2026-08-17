package com.example.ebearrestapi.gateway.inicis;

import com.example.ebearrestapi.dto.request.PaymentConfirmDto;
import com.example.ebearrestapi.etc.PgProvider;
import com.example.ebearrestapi.gateway.PaymentGateway;
import com.example.ebearrestapi.gateway.dto.PayoutResponseDto;
import com.example.ebearrestapi.gateway.dto.PaymentResponseDto;
import com.example.ebearrestapi.gateway.dto.PgSettlementDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class InicisPaymentGateway implements PaymentGateway {
    @Override
    public boolean supports(PgProvider pgProvider) {
        return pgProvider == PgProvider.INICIS;
    }

    @Override
    public PaymentResponseDto confirm(PaymentConfirmDto confirmDto) {
        throw new UnsupportedOperationException("KG이니시스 결제는 현재 준비 중입니다.");
    }

    @Override
    public void cancel(String paymentKey, String reason) {
    }

    @Override
    public List<PgSettlementDto> getSettlementHistory(LocalDate startDate, LocalDate endDate) {
        return Collections.emptyList();
    }

    @Override
    public PayoutResponseDto requestPayout(String bankName, String accountNumber, String accountHolder, int amount) {
        throw new UnsupportedOperationException("KG이니시스 정산 지급은 현재 준비 중입니다.");
    }
}
