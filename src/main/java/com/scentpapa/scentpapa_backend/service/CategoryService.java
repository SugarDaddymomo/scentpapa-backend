package com.scentpapa.scentpapa_backend.service;

import com.scentpapa.scentpapa_backend.models.Category;
import java.util.List;

public interface CategoryService {
    Category createCategory(String name, Long parentId);
    List<Category> getAllCategories();
}