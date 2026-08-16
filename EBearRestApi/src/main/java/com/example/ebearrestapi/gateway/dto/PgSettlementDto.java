package com.example.ebearrestapi.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgSettlementDto {
    private String paymentKey;
    private String orderId;
    private Integer amount;
    private Integer fee;
    private Integer payOutAmount;
    private LocalDate soldDate;
    private LocalDate paidOutDate;
    private LocalDateTime approvedAt;
}
