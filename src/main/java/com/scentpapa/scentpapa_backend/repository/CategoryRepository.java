package com.scentpapa.scentpapa_backend.repository;


import com.scentpapa.scentpapa_backend.models.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @EntityGraph(attributePaths = {"parent", "children"})
    Optional<Category> findById(Long id);
}
