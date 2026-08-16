package com.example.ebearrestapi.controller;

import com.example.ebearrestapi.dto.request.SellerAccountDto;
import com.example.ebearrestapi.dto.response.SettlementDetailDto;
import com.example.ebearrestapi.dto.response.SettlementSummaryDto;
import com.example.ebearrestapi.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @GetMapping("/list")
    public ResponseEntity<Page<SettlementSummaryDto>> getMySettlementList(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return ResponseEntity.ok(settlementService.getMySettlementList(user, pageable));
    }

    @GetMapping("/{settlementNo}")
    public ResponseEntity<SettlementDetailDto> getSettlementDetail(
            @PathVariable Long settlementNo,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(settlementService.getSettlementDetail(settlementNo, user));
    }

    @PostMapping("/account")
    public ResponseEntity<String> registerAccount(
            @RequestBody SellerAccountDto accountDto,
            @AuthenticationPrincipal User user) {
        settlementService.registerOrUpdateAccount(accountDto, user);
        return ResponseEntity.ok("OK");
    }
}
