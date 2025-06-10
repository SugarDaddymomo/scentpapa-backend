package com.scentpapa.scentpapa_backend.service;

import com.scentpapa.scentpapa_backend.dto.ProductDTO;
import com.scentpapa.scentpapa_backend.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    Page<ProductDTO> getProducts(int page, int size);
    ProductDTO getProductById(Long id);
    ProductDTO createProduct(String name, String description, Double price, Integer stock, String notes, Long categoryId, MultipartFile[] images);
    ProductDTO updateProduct(Long id, String name, String description, Double price, Integer stock, String notes, Long categoryId);
    void deleteProduct(Long id);
    Page<ProductDTO> getProductsByCategory(Long categoryId, int page, int size);
}
