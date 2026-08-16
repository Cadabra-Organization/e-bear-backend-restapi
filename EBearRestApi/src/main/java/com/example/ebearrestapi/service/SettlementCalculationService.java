package com.example.ebearrestapi.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class SettlementCalculationService {

    private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.05");
    private static final BigDecimal PG_FEE_RATE = new BigDecimal("0.033");

    public int calculatePlatformFee(int amount) {
        return BigDecimal.valueOf(amount)
                .multiply(PLATFORM_FEE_RATE)
                .setScale(0, RoundingMode.FLOOR)
                .intValue();
    }

    public int calculatePgFee(int amount) {
        return BigDecimal.valueOf(amount)
                .multiply(PG_FEE_RATE)
                .setScale(0, RoundingMode.FLOOR)
                .intValue();
    }

    public int calculateFinalPayout(int totalSales, int platformFee, int pgFee) {
        return totalSales - (platformFee + pgFee);
    }
}
