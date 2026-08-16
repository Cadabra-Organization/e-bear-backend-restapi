package com.example.ebearrestapi.entity;

import com.example.ebearrestapi.etc.SettlementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SETTLEMENT", indexes = {
    @Index(name = "idx_settlement_seller_date", columnList = "sellerNo, settlementDate"),
    @Index(name = "idx_settlement_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SettlementEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settlementNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sellerNo", nullable = false)
    private UserEntity seller;

    @Column(nullable = false)
    private LocalDate settlementDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private SettlementStatus status = SettlementStatus.PENDING;

    @Column(nullable = false)
    private Integer totalSalesAmount;

    @Column(nullable = false)
    private Integer platformFeeAmount;

    @Column(nullable = false)
    private Integer pgFeeAmount;

    @Column(nullable = false)
    private Integer finalPayoutAmount;

    private LocalDateTime confirmedAt;
    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SettlementItemEntity> settlementItems = new ArrayList<>();

    public void addSettlementItem(SettlementItemEntity item) {
        this.settlementItems.add(item);
        item.setSettlement(this);
    }

    public void confirmSettlement() {
        this.status = SettlementStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void completePayout() {
        this.status = SettlementStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }
}
