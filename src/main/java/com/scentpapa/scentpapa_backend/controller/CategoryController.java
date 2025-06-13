package com.scentpapa.scentpapa_backend.controller;

import com.scentpapa.scentpapa_backend.models.Category;
import com.scentpapa.scentpapa_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scentpapa/v1/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    // Create a new category
    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('PERMISSION_CATEGORY')")
    public ResponseEntity<Category> createCategory(
            @RequestParam String name,
            @RequestParam(required = false) Long parentId) {
        Category category = categoryService.createCategory(name, parentId);
        return ResponseEntity.ok(category);
    }

    // Optionally: Fetch all categories (also restricted to admins)
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('PERMISSION_CATEGORY')")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllCategoriesForCustomers() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}