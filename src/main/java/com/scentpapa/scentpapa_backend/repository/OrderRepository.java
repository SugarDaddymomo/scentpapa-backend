package com.scentpapa.scentpapa_backend.repository;

import com.scentpapa.scentpapa_backend.models.Order;
import com.scentpapa.scentpapa_backend.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByRazorpayOrderId(String razorpayId);
    Page<Order> findAllByUser(User user, Pageable pageable);

}