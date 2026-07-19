package com.example.ebearrestapi.repository;

import com.example.ebearrestapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query(value = "SELECT * FROM USERS WHERE user_id = :userId", nativeQuery = true)
    Optional<UserEntity> findByUserId(@Param("userId") String userId);
}
