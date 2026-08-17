package com.example.ebearrestapi.service;

import com.example.ebearrestapi.dto.request.SellerAccountDto;
import com.example.ebearrestapi.dto.response.SettlementDetailDto;
import com.example.ebearrestapi.dto.response.SettlementSummaryDto;
import com.example.ebearrestapi.entity.SellerAccountEntity;
import com.example.ebearrestapi.entity.SettlementEntity;
import com.example.ebearrestapi.entity.SettlementItemEntity;
import com.example.ebearrestapi.entity.UserEntity;
import com.example.ebearrestapi.repository.SellerAccountRepository;
import com.example.ebearrestapi.repository.SettlementItemRepository;
import com.example.ebearrestapi.repository.SettlementRepository;
import com.example.ebearrestapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final SettlementItemRepository settlementItemRepository;
    private final SellerAccountRepository sellerAccountRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<SettlementSummaryDto> getMySettlementList(User user, Pageable pageable) {
        UserEntity seller = userRepository.findByUserId(user.getUsername())
                .orElseThrow(() -> new RuntimeException("판매자 정보를 찾을 수 없습니다."));

        return settlementRepository.findAllBySellerOrderBySettlementDateDesc(seller, pageable)
                .map(this::toSummaryDto);
    }

    @Transactional(readOnly = true)
    public SettlementDetailDto getSettlementDetail(Long settlementNo, User user) {
        UserEntity seller = userRepository.findByUserId(user.getUsername())
                .orElseThrow(() -> new RuntimeException("판매자 정보를 찾을 수 없습니다."));

        SettlementEntity settlement = settlementRepository.findById(settlementNo)
                .orElseThrow(() -> new RuntimeException("정산 내역을 찾을 수 없습니다."));

        if (!settlement.getSeller().getUserNo().equals(seller.getUserNo()) && !seller.isAdmin()) {
            throw new RuntimeException("본인의 정산 내역만 조회할 수 있습니다.");
        }

        List<SettlementItemEntity> items = settlementItemRepository.findBySettlement(settlement);
        List<SettlementDetailDto.SettlementItemDto> itemDtos = items.stream().map(item ->
                SettlementDetailDto.SettlementItemDto.builder()
                        .settlementItemNo(item.getSettlementItemNo())
                        .orderPaymentId(item.getOrderPaymentId())
                        .productName(item.getProductName())
                        .optionName(item.getOptionName())
                        .quantity(item.getQuantity())
                        .itemPrice(item.getItemPrice())
                        .itemTotalAmount(item.getItemTotalAmount())
                        .platformFee(item.getPlatformFee())
                        .pgFee(item.getPgFee())
                        .netPayoutAmount(item.getNetPayoutAmount())
                        .build()
        ).toList();

        return SettlementDetailDto.builder()
                .summary(toSummaryDto(settlement))
                .items(itemDtos)
                .build();
    }

    @Transactional
    public void registerOrUpdateAccount(SellerAccountDto dto, User user) {
        UserEntity seller = userRepository.findByUserId(user.getUsername())
                .orElseThrow(() -> new RuntimeException("판매자 정보를 찾을 수 없습니다."));

        SellerAccountEntity account = sellerAccountRepository.findBySeller(seller)
                .orElseGet(() -> SellerAccountEntity.builder()
                        .seller(seller)
                        .bankName(dto.getBankName())
                        .accountNumber(dto.getAccountNumber())
                        .accountHolder(dto.getAccountHolder())
                        .build());

        account.updateAccount(dto.getBankName(), dto.getAccountNumber(), dto.getAccountHolder());
        sellerAccountRepository.save(account);
    }

    private SettlementSummaryDto toSummaryDto(SettlementEntity s) {
        return SettlementSummaryDto.builder()
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
                .build();
    }
}
