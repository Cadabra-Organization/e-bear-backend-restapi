package com.example.ebearrestapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementReconciliationDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalPgTransactions;
    private int matchedCount;
    private int unmatchedCount;
    private long totalPgAmount;
    private long totalPgFee;
    private long totalPgPayoutAmount;
    private List<DiscrepancyItemDto> discrepancies;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiscrepancyItemDto {
        private String orderId;
        private String paymentKey;
        private Integer dbAmount;
        private Integer pgAmount;
        private String reason;
    }
}
