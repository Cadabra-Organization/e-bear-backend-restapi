package com.example.ebearrestapi.repository;

import com.example.ebearrestapi.entity.SettlementEntity;
import com.example.ebearrestapi.entity.UserEntity;
import com.example.ebearrestapi.etc.SettlementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<SettlementEntity, Long> {
    Page<SettlementEntity> findAllBySellerOrderBySettlementDateDesc(UserEntity seller, Pageable pageable);
    Page<SettlementEntity> findAllByStatusOrderBySettlementDateDesc(SettlementStatus status, Pageable pageable);
    Optional<SettlementEntity> findBySellerAndSettlementDate(UserEntity seller, LocalDate settlementDate);

    @Query("SELECT SUM(s.finalPayoutAmount) FROM SettlementEntity s WHERE s.seller.userNo = :sellerNo")
    Integer sumTotalPayoutBySeller(@Param("sellerNo") Long sellerNo);
}
