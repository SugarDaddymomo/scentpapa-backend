package com.scentpapa.scentpapa_backend.controller;

import com.scentpapa.scentpapa_backend.dto.CartDTO;
import com.scentpapa.scentpapa_backend.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scentpapa/v1/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartDTO> getUserCart() {
        CartDTO cartDTO = cartService.getUserCart();
        return ResponseEntity.ok(cartDTO);
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartDTO> addItemToCart(
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        log.info("Adding item to cart");
        CartDTO cartDTO = cartService.addItemToCart(productId, quantity);
        return ResponseEntity.ok(cartDTO);
    }

    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartDTO> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        CartDTO cartDTO = cartService.updateItemQuantity(itemId, quantity);
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long itemId) {
        cartService.removeItemFromCart(itemId);
        return ResponseEntity.noContent().build();
    }
}