package com.scentpapa.scentpapa_backend.service.impl;

import com.scentpapa.scentpapa_backend.dto.CartDTO;
import com.scentpapa.scentpapa_backend.dto.CartItemDTO;
import com.scentpapa.scentpapa_backend.models.Cart;
import com.scentpapa.scentpapa_backend.models.CartItem;
import com.scentpapa.scentpapa_backend.models.Product;
import com.scentpapa.scentpapa_backend.models.User;
import com.scentpapa.scentpapa_backend.repository.CartItemRepository;
import com.scentpapa.scentpapa_backend.repository.CartRepository;
import com.scentpapa.scentpapa_backend.repository.ProductRepository;
import com.scentpapa.scentpapa_backend.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final RatingServiceImpl ratingService;

    @Override
    @Transactional(readOnly = true)
    public CartDTO getUserCart() {
        User user = ratingService.getCurrentAuthenticatedUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> Cart.builder().user(user).build());
        return mapToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(Long productId, int quantity) {
        log.info("Let's add item to cart");
        User user = ratingService.getCurrentAuthenticatedUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>());
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem existingItem = cart.getCartItems() != null ?
                cart.getCartItems().stream()
                        .filter(item -> item.getProduct().equals(product))
                        .findFirst()
                        .orElse(null) : null;

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cart.getCartItems().add(newItem);
        }

        cartRepository.save(cart);
        return mapToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO updateItemQuantity(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return mapToCartDTO(cartItem.getCart());
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public void clearUserCart() {
        User user = ratingService.getCurrentAuthenticatedUser();
        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart != null && cart.getCartItems() != null) {
            cart.getCartItems().clear();
            cartRepository.save(cart);
        }
    }


    private CartDTO mapToCartDTO(Cart cart) {
        double totalAmount = calculateTotalAmount(cart);
        List<CartItemDTO> items = cart.getCartItems().stream()
                .map(item -> CartItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getProduct().getPrice())
                        .build())
                .toList();
        return CartDTO.builder()
                .id(cart.getId())
                .items(items)
                .totalAmount(totalAmount)
                .build();
    }

    private double calculateTotalAmount(Cart cart) {
        return cart.getCartItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}