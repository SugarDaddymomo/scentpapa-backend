package com.scentpapa.scentpapa_backend.repository;

import com.scentpapa.scentpapa_backend.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"images"})
    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
}