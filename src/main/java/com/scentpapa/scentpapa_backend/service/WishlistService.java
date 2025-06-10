package com.scentpapa.scentpapa_backend.service;

import com.scentpapa.scentpapa_backend.dto.ProductDTO;
import com.scentpapa.scentpapa_backend.dto.WishlistDTO;
import com.scentpapa.scentpapa_backend.models.Product;
import com.scentpapa.scentpapa_backend.models.WishList;
import java.util.List;

public interface WishlistService {
    WishlistDTO addToWishList(Long productId);
    void removeFromWishList(Long productId);
    List<ProductDTO> getUserWishList();
}