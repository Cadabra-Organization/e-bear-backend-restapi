package com.example.ebearrestapi.repository;

import com.example.ebearrestapi.entity.SellerAccountEntity;
import com.example.ebearrestapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerAccountRepository extends JpaRepository<SellerAccountEntity, Long> {
    Optional<SellerAccountEntity> findBySeller(UserEntity seller);
}
