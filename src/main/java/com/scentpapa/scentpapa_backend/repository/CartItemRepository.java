package com.scentpapa.scentpapa_backend.repository;

import com.scentpapa.scentpapa_backend.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}