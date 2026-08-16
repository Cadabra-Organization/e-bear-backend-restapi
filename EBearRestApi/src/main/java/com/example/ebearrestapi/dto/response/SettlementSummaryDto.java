package com.example.ebearrestapi.dto.response;

import com.example.ebearrestapi.etc.SettlementStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class SettlementSummaryDto {
    private Long settlementNo;
    private Long sellerNo;
    private String sellerName;
    private LocalDate settlementDate;
    private SettlementStatus status;
    private String statusDescription;
    private Integer totalSalesAmount;
    private Integer platformFeeAmount;
    private Integer pgFeeAmount;
    private Integer finalPayoutAmount;
    private LocalDateTime confirmedAt;
    private LocalDateTime paidAt;
}
