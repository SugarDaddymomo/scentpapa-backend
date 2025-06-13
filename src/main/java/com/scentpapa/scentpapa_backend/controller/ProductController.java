package com.scentpapa.scentpapa_backend.controller;

import com.scentpapa.scentpapa_backend.dto.ProductDTO;
import com.scentpapa.scentpapa_backend.models.Product;
import com.scentpapa.scentpapa_backend.requests.CreateRatingRequest;
import com.scentpapa.scentpapa_backend.response.RatingResponse;
import com.scentpapa.scentpapa_backend.service.ProductService;
import com.scentpapa.scentpapa_backend.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/scentpapa/v1/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final RatingService ratingService;

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDTO>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (categoryId != null) {
            return ResponseEntity.ok(productService.getProductsByCategory(categoryId, page, size));
        } else {
            return ResponseEntity.ok(productService.getProducts(page, size));
        }
    }


    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/admin/products")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('PERMISSION_PRODUCTS')")
    public ResponseEntity<ProductDTO> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam("notes") String notes,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "images", required = false) MultipartFile[] images
    ) {
        return ResponseEntity.ok(productService.createProduct(name, description, price, stock, notes, categoryId, images));
    }

    @PutMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('PERMISSION_PRODUCTS')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam("notes") String notes,
            @RequestParam("categoryId") Long categoryId) {
        return ResponseEntity.ok(productService.updateProduct(id, name, description, price, stock, notes, categoryId));
    }

    @DeleteMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('PERMISSION_PRODUCTS')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/products/{productId}/ratings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RatingResponse> createRating(@PathVariable Long productId, @ModelAttribute CreateRatingRequest ratingRequest) throws IOException {
        log.info("Rating shuru 1");
        RatingResponse ratingResponse = ratingService.createRating(productId, ratingRequest);
        return ResponseEntity.ok(ratingResponse);
    }

    @GetMapping("/products/{productId}/ratings")
    public ResponseEntity<Page<RatingResponse>> getRatings(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<RatingResponse> ratings = ratingService.getRatings(productId, page, size);
        return ResponseEntity.ok(ratings);
    }
}