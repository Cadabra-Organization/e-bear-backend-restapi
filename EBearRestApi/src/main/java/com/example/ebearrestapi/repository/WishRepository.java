package com.example.ebearrestapi.repository;

import com.example.ebearrestapi.entity.WishListEntity;
import com.example.ebearrestapi.etc.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WishRepository extends JpaRepository<WishListEntity, Long> {
    @EntityGraph(attributePaths = {"product", "product.user", "product.fileList", "product.productOptionList", "product.user.file"})
    @Query("SELECT w FROM WishListEntity w " +
            "WHERE w.user.userId = :userId " +
            "AND w.wishListNo < :cursor " +
            "AND w.product.user.role = :role " +
            "ORDER BY w.wishListNo DESC")
    Slice<WishListEntity> findWishList(@Param("userId") String userId,
                                       @Param("cursor") Long cursor,
                                       @Param("role") Role role,
                                       Pageable pageable);

    Optional<WishListEntity> findByWishListNoAndUser_UserId(Long wishListNo, String userId);

    boolean existsByUser_UserIdAndProduct_ProductNo(String userId, Long productNo);
}
