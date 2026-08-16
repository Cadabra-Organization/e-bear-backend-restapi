package com.example.ebearrestapi.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutResponseDto {
    private boolean isSuccess;
    private String payoutId;
    private Integer amount;
    private String receiverBank;
    private String receiverAccount;
    private String errorMessage;
    private LocalDateTime paidAt;
}
