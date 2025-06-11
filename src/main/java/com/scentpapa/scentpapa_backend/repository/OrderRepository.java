package com.scentpapa.scentpapa_backend.repository;

import com.scentpapa.scentpapa_backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}