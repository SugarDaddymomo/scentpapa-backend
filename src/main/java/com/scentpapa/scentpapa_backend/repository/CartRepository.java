package com.scentpapa.scentpapa_backend.repository;

import com.scentpapa.scentpapa_backend.models.Cart;
import com.scentpapa.scentpapa_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}