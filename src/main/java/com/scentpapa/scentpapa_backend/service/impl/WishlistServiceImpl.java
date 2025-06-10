package com.scentpapa.scentpapa_backend.service.impl;

import com.scentpapa.scentpapa_backend.dto.ProductDTO;
import com.scentpapa.scentpapa_backend.dto.WishlistDTO;
import com.scentpapa.scentpapa_backend.models.Product;
import com.scentpapa.scentpapa_backend.models.User;
import com.scentpapa.scentpapa_backend.models.WishList;
import com.scentpapa.scentpapa_backend.repository.ProductRepository;
import com.scentpapa.scentpapa_backend.repository.WishlistRepository;
import com.scentpapa.scentpapa_backend.service.WishlistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final RatingServiceImpl ratingService;
    private final ProductServiceImpl productService;

    @Override
    @Transactional
    public WishlistDTO addToWishList(Long productId) {
        User user = ratingService.getCurrentAuthenticatedUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if already exists
        if (wishlistRepository.findByUserAndProduct(user, product).isPresent()) {
            throw new RuntimeException("Product already in wishlist");
        }
        WishList wishList = WishList.builder()
                .user(user)
                .product(product)
                .build();
        wishlistRepository.save(wishList);
        return mapToWishListDTO(product);
    }

    @Override
    @Transactional
    public void removeFromWishList(Long productId) {
        User user = ratingService.getCurrentAuthenticatedUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        wishlistRepository.deleteByUserAndProduct(user, product);
    }

    @Override
    public List<ProductDTO> getUserWishList() {
        User user = ratingService.getCurrentAuthenticatedUser();
        List<WishList> wishLists = wishlistRepository.findByUser(user);
        return wishLists.stream()
                .map(wishList -> productService.mapToProductDTO(wishList.getProduct()))
                .toList();
    }

    private WishlistDTO mapToWishListDTO(Product product) {
        return WishlistDTO.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .productDescription(product.getDescription())
                .build();
    }
}