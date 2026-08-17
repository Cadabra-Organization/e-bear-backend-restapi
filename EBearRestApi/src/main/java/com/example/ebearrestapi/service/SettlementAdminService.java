package com.example.ebearrestapi.service;

import com.example.ebearrestapi.dto.response.SettlementSummaryDto;
import com.example.ebearrestapi.entity.OrderItemEntity;
import com.example.ebearrestapi.entity.SellerAccountEntity;
import com.example.ebearrestapi.entity.SettlementEntity;
import com.example.ebearrestapi.entity.SettlementItemEntity;
import com.example.ebearrestapi.entity.UserEntity;
import com.example.ebearrestapi.etc.PgProvider;
import com.example.ebearrestapi.etc.SettlementStatus;
import com.example.ebearrestapi.gateway.PaymentGateway;
import com.example.ebearrestapi.gateway.PaymentGatewayRegistry;
import com.example.ebearrestapi.gateway.dto.PayoutResponseDto;
import com.example.ebearrestapi.repository.OrderItemRepository;
import com.example.ebearrestapi.repository.SellerAccountRepository;
import com.example.ebearrestapi.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementAdminService {

    private final SettlementRepository settlementRepository;
    private final OrderItemRepository orderItemRepository;
    private final SellerAccountRepository sellerAccountRepository;
    private final SettlementCalculationService calculationService;
    private final PaymentGatewayRegistry paymentGatewayRegistry;

    @Transactional
    public int executeSettlement(LocalDate targetDate) {
        LocalDate settlementDate = (targetDate != null) ? targetDate : LocalDate.now();

        List<OrderItemEntity> targetItems = orderItemRepository.findSettlementTargetOrderItems();
        if (targetItems.isEmpty()) {
            return 0;
        }

        Map<UserEntity, List<OrderItemEntity>> itemsBySeller = targetItems.stream()
                .collect(Collectors.groupingBy(item -> item.getProductOption().getProduct().getUser()));

        int createdSettlementCount = 0;

        for (Map.Entry<UserEntity, List<OrderItemEntity>> entry : itemsBySeller.entrySet()) {
            UserEntity seller = entry.getKey();
            List<OrderItemEntity> sellerItems = entry.getValue();

            int totalSales = 0;
            int totalPlatformFee = 0;
            int totalPgFee = 0;

            SettlementEntity settlement = SettlementEntity.builder()
                    .seller(seller)
                    .settlementDate(settlementDate)
                    .status(SettlementStatus.PENDING)
                    .totalSalesAmount(0)
                    .platformFeeAmount(0)
                    .pgFeeAmount(0)
                    .finalPayoutAmount(0)
                    .build();

            for (OrderItemEntity item : sellerItems) {
                int itemPrice = item.getProductOption().getProductOptionPrice();
                int qty = item.getQuantity();
                int itemTotal = itemPrice * qty;

                int platformFee = calculationService.calculatePlatformFee(itemTotal);
                int pgFee = calculationService.calculatePgFee(itemTotal);
                int netPayout = itemTotal - platformFee - pgFee;

                totalSales += itemTotal;
                totalPlatformFee += platformFee;
                totalPgFee += pgFee;

                SettlementItemEntity itemEntity = SettlementItemEntity.builder()
                        .settlement(settlement)
                        .orderPaymentId(item.getOrderPayment().getOrderPaymentId())
                        .orderItemNo(item.getOrderItemNo())
                        .productNo(item.getProductOption().getProduct().getProductNo())
                        .productName(item.getProductOption().getProduct().getProductName())
                        .optionName(item.getProductOption().getProductOptionName())
                        .quantity(qty)
                        .itemPrice(itemPrice)
                        .itemTotalAmount(itemTotal)
                        .platformFee(platformFee)
                        .pgFee(pgFee)
                        .netPayoutAmount(netPayout)
                        .build();

                settlement.addSettlementItem(itemEntity);
                item.markAsSettled();
            }

            int finalPayout = calculationService.calculateFinalPayout(totalSales, totalPlatformFee, totalPgFee);

            settlement.setTotalSalesAmount(totalSales);
            settlement.setPlatformFeeAmount(totalPlatformFee);
            settlement.setPgFeeAmount(totalPgFee);
            settlement.setFinalPayoutAmount(finalPayout);

            settlementRepository.save(settlement);
            createdSettlementCount++;
        }

        return createdSettlementCount;
    }

    @Transactional(readOnly = true)
    public Page<SettlementSummaryDto> getAllSettlements(SettlementStatus status, Pageable pageable) {
        Page<SettlementEntity> page = (status != null)
                ? settlementRepository.findAllByStatusOrderBySettlementDateDesc(status, pageable)
                : settlementRepository.findAll(pageable);

        return page.map(s -> SettlementSummaryDto.builder()
                .settlementNo(s.getSettlementNo())
                .sellerNo(s.getSeller().getUserNo())
                .sellerName(s.getSeller().getUserName())
                .settlementDate(s.getSettlementDate())
                .status(s.getStatus())
                .statusDescription(s.getStatus().getDescription())
                .totalSalesAmount(s.getTotalSalesAmount())
                .platformFeeAmount(s.getPlatformFeeAmount())
                .pgFeeAmount(s.getPgFeeAmount())
                .finalPayoutAmount(s.getFinalPayoutAmount())
                .confirmedAt(s.getConfirmedAt())
                .paidAt(s.getPaidAt())
                .build());
    }

    @Transactional
    public void confirmSettlement(Long settlementNo) {
        SettlementEntity settlement = settlementRepository.findById(settlementNo)
                .orElseThrow(() -> new RuntimeException("정산 내역을 찾을 수 없습니다."));
        settlement.confirmSettlement();
    }

    @Transactional
    public PayoutResponseDto completePayout(Long settlementNo) {
        SettlementEntity settlement = settlementRepository.findById(settlementNo)
                .orElseThrow(() -> new RuntimeException("정산 내역을 찾을 수 없습니다."));

        UserEntity seller = settlement.getSeller();
        SellerAccountEntity account = sellerAccountRepository.findBySeller(seller)
                .orElseThrow(() -> new RuntimeException("판매자의 등록된 정산 계좌가 없습니다."));

        PaymentGateway gateway = paymentGatewayRegistry.getGateway(PgProvider.TOSS);
        PayoutResponseDto payoutResponse = gateway.requestPayout(
                account.getBankName(),
                account.getAccountNumber(),
                account.getAccountHolder(),
                settlement.getFinalPayoutAmount()
        );

        if (!payoutResponse.isSuccess()) {
            throw new RuntimeException("PG사 지급 대행 요청 실패: " + payoutResponse.getErrorMessage());
        }

        settlement.completePayout();
        return payoutResponse;
    }
}
