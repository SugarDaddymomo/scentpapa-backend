package com.scentpapa.scentpapa_backend.service;

import com.scentpapa.scentpapa_backend.dto.CartDTO;

public interface CartService {
    CartDTO getUserCart();
    CartDTO addItemToCart(Long productId, int quantity);
    CartDTO updateItemQuantity(Long cartItemId, int quantity);
    void removeItemFromCart(Long cartItemId);
}