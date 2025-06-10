package com.scentpapa.scentpapa_backend.repository;

import com.scentpapa.scentpapa_backend.models.Product;
import com.scentpapa.scentpapa_backend.models.User;
import com.scentpapa.scentpapa_backend.models.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<WishList, Long> {
    List<WishList> findByUser(User user);
    Optional<WishList> findByUserAndProduct(User user, Product product);
    void deleteByUserAndProduct(User user, Product product);
}