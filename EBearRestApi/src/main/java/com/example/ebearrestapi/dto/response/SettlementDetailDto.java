package com.example.ebearrestapi.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SettlementDetailDto {
    private SettlementSummaryDto summary;
    private List<SettlementItemDto> items;

    @Getter
    @Builder
    public static class SettlementItemDto {
        private Long settlementItemNo;
        private String orderPaymentId;
        private String productName;
        private String optionName;
        private Integer quantity;
        private Integer itemPrice;
        private Integer itemTotalAmount;
        private Integer platformFee;
        private Integer pgFee;
        private Integer netPayoutAmount;
    }
}
