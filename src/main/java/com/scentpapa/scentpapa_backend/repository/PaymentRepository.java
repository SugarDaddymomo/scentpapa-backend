package com.scentpapa.scentpapa_backend.repository;

import com.scentpapa.scentpapa_backend.models.Order;
import com.scentpapa.scentpapa_backend.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);
}