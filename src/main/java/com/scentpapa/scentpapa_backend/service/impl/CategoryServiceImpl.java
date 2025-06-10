package com.scentpapa.scentpapa_backend.service.impl;

import com.scentpapa.scentpapa_backend.models.Category;
import com.scentpapa.scentpapa_backend.repository.CategoryRepository;
import com.scentpapa.scentpapa_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(String name, Long parentId) {
        Category category = new Category();
        category.setName(name);
        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parent);
        }
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}