package com.example.ebearrestapi.gateway;

import com.example.ebearrestapi.dto.request.PaymentConfirmDto;
import com.example.ebearrestapi.etc.PgProvider;
import com.example.ebearrestapi.gateway.dto.PayoutResponseDto;
import com.example.ebearrestapi.gateway.dto.PaymentResponseDto;
import com.example.ebearrestapi.gateway.dto.PgSettlementDto;

import java.time.LocalDate;
import java.util.List;

public interface PaymentGateway {
    boolean supports(PgProvider pgProvider);
    PaymentResponseDto confirm(PaymentConfirmDto confirmDto);
    void cancel(String paymentKey, String reason);
    List<PgSettlementDto> getSettlementHistory(LocalDate startDate, LocalDate endDate);
    PayoutResponseDto requestPayout(String bankName, String accountNumber, String accountHolder, int amount);
}
