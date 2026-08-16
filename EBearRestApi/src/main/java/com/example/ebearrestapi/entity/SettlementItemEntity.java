package com.example.ebearrestapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SETTLEMENT_ITEM")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SettlementItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settlementItemNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlementNo", nullable = false)
    private SettlementEntity settlement;

    @Column(nullable = false)
    private String orderPaymentId;

    @Column(nullable = false)
    private Long orderItemNo;

    @Column(nullable = false)
    private Long productNo;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String optionName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer itemPrice;

    @Column(nullable = false)
    private Integer itemTotalAmount;

    @Column(nullable = false)
    private Integer platformFee;

    @Column(nullable = false)
    private Integer pgFee;

    @Column(nullable = false)
    private Integer netPayoutAmount;
}
