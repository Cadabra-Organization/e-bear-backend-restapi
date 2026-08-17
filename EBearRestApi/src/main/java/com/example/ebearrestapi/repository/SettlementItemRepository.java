package com.example.ebearrestapi.repository;

import com.example.ebearrestapi.entity.SettlementEntity;
import com.example.ebearrestapi.entity.SettlementItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementItemRepository extends JpaRepository<SettlementItemEntity, Long> {
    List<SettlementItemEntity> findBySettlement(SettlementEntity settlement);
}
