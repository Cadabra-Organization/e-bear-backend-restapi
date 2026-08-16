package com.example.ebearrestapi.controller;

import com.example.ebearrestapi.dto.response.SettlementReconciliationDto;
import com.example.ebearrestapi.dto.response.SettlementSummaryDto;
import com.example.ebearrestapi.etc.PgProvider;
import com.example.ebearrestapi.etc.SettlementStatus;
import com.example.ebearrestapi.gateway.dto.PayoutResponseDto;
import com.example.ebearrestapi.service.SettlementAdminService;
import com.example.ebearrestapi.service.SettlementReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/settlements")
@RequiredArgsConstructor
public class SettlementAdminController {

    private final SettlementAdminService settlementAdminService;
    private final SettlementReconciliationService reconciliationService;

    @PostMapping("/execute")
    public ResponseEntity<?> executeSettlement(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        int count = settlementAdminService.executeSettlement(targetDate);
        return ResponseEntity.ok(Map.of(
                "message", "SUCCESS",
                "createdSettlementsCount", count
        ));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<SettlementSummaryDto>> getAllSettlements(
            @RequestParam(required = false) SettlementStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(settlementAdminService.getAllSettlements(status, pageable));
    }

    @PostMapping("/{settlementNo}/confirm")
    public ResponseEntity<String> confirmSettlement(@PathVariable Long settlementNo) {
        settlementAdminService.confirmSettlement(settlementNo);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/{settlementNo}/payout")
    public ResponseEntity<PayoutResponseDto> completePayout(@PathVariable Long settlementNo) {
        PayoutResponseDto response = settlementAdminService.completePayout(settlementNo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reconciliation")
    public ResponseEntity<SettlementReconciliationDto> reconcileSettlement(
            @RequestParam(defaultValue = "TOSS") PgProvider pgProvider,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        SettlementReconciliationDto result = reconciliationService.reconcileSettlement(pgProvider, startDate, endDate);
        return ResponseEntity.ok(result);
    }
}
