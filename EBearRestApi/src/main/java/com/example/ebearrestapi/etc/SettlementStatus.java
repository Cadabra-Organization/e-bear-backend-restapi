package com.example.ebearrestapi.etc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementStatus {
    PENDING("정산 대기"),
    CONFIRMED("정산 확정"),
    PAID("지급 완료"),
    CANCELED("정산 취소");

    private final String description;
}
