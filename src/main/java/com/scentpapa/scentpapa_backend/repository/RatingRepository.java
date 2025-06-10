package com.scentpapa.scentpapa_backend.repository;

import com.scentpapa.scentpapa_backend.models.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Page<Rating> findByProductId(Long productId, PageRequest pageable);
}