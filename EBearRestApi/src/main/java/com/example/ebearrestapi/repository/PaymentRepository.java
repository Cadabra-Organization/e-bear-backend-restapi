package com.example.ebearrestapi.repository;

import com.example.ebearrestapi.entity.OrderPaymentEntity;
import com.example.ebearrestapi.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByOrderPayment_OrderPaymentId(String orderPaymentId);
    Optional<PaymentEntity> findByOrderPayment(OrderPaymentEntity orderPayment);
    Optional<PaymentEntity> findByPaymentKey(String paymentKey);
}
