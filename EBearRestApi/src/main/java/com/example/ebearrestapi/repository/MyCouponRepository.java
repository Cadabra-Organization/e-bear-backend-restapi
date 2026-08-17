package com.example.ebearrestapi.repository;

import com.example.ebearrestapi.entity.MyCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyCouponRepository extends JpaRepository<MyCouponEntity, Integer> {
}
