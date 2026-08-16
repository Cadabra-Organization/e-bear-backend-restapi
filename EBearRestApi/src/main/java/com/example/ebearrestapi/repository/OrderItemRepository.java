package com.example.ebearrestapi.repository;

import com.example.ebearrestapi.entity.OrderItemEntity;
import com.example.ebearrestapi.entity.OrderPaymentEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    @EntityGraph(attributePaths = {"productOption", "productOption.product"})
    List<OrderItemEntity> findByOrderPayment(OrderPaymentEntity orderPayment);

    @EntityGraph(attributePaths = {"productOption", "productOption.product"})
    List<OrderItemEntity> findAllByOrderItemNo(Long orderItemNo);

    @Query("SELECT oi FROM OrderItemEntity oi " +
           "JOIN FETCH oi.orderPayment op " +
           "JOIN PaymentEntity p ON p.orderPayment = op " +
           "JOIN FETCH oi.productOption po " +
           "JOIN FETCH po.product prod " +
           "JOIN FETCH prod.user seller " +
           "WHERE p.paymentStatus = com.example.ebearrestapi.etc.PaymentStatus.DONE " +
           "AND oi.isSettled = false")
    List<OrderItemEntity> findSettlementTargetOrderItems();
}
