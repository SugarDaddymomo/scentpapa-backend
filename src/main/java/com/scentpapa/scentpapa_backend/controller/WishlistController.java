package com.scentpapa.scentpapa_backend.controller;

import com.scentpapa.scentpapa_backend.dto.ProductDTO;
import com.scentpapa.scentpapa_backend.dto.WishlistDTO;
import com.scentpapa.scentpapa_backend.models.WishList;
import com.scentpapa.scentpapa_backend.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/scentpapa/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<WishlistDTO> addToWishList(@PathVariable Long productId) {
        WishlistDTO wishList = wishlistService.addToWishList(productId);
        return ResponseEntity.ok(wishList);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> removeFromWishList(@PathVariable Long productId) {
        wishlistService.removeFromWishList(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<ProductDTO>> getUserWishList() {
        List<ProductDTO> products = wishlistService.getUserWishList();
        return ResponseEntity.ok(products);
    }
}