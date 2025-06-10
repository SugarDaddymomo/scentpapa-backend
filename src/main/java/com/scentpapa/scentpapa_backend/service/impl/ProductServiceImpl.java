package com.scentpapa.scentpapa_backend.service.impl;

import com.scentpapa.scentpapa_backend.dto.ProductDTO;
import com.scentpapa.scentpapa_backend.dto.ProductImageDTO;
import com.scentpapa.scentpapa_backend.models.Category;
import com.scentpapa.scentpapa_backend.models.Product;
import com.scentpapa.scentpapa_backend.models.ProductImage;
import com.scentpapa.scentpapa_backend.repository.CategoryRepository;
import com.scentpapa.scentpapa_backend.repository.ProductRepository;
import com.scentpapa.scentpapa_backend.service.ProductService;
import com.scentpapa.scentpapa_backend.util.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final S3Service s3Service;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<ProductDTO> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::mapToProductDTO);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToProductDTO(product);
    }

    @Override
    public ProductDTO createProduct(String name, String description, Double price, Integer stock, String notes, Long categoryId, MultipartFile[] images) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stock);
        product.setNotes(notes);
        product.setCategory(category);
        product = productRepository.save(product);
        if (images != null && images.length > 0) {
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : images) {
                try {
                    String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
                    String imageUrl = s3Service.uploadFile(file.getBytes(), Objects.requireNonNull(file.getOriginalFilename()), contentType);
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(imageUrl);
                    productImage.setProduct(product);
                    productImages.add(productImage);
                } catch (IOException ex) {
                    log.error("Error while uploading file to s3: {}", ex.getMessage());
                }
            }
            product.getImages().addAll(productImages);
            product = productRepository.save(product);
        }
        return mapToProductDTO(product);
    }

    @Override
    public ProductDTO updateProduct(Long id, String name, String description, Double price, Integer stock, String notes, Long categoryId) {
        Product existing = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
            existing.setCategory(category);
        } if (StringUtils.hasText(name)) {
            existing.setName(name);
        } if (StringUtils.hasText(description)) {
            existing.setDescription(description);
        } if (price != null) {
            existing.setPrice(price);
        } if (stock != null) {
            existing.setStockQuantity(stock);
        } if (StringUtils.hasText(notes)) {
            existing.setNotes(notes);
        }
        existing = productRepository.save(existing);
        return mapToProductDTO(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductDTO> getProductsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByCategory_Id(categoryId, pageable);
        return products.map(this::mapToProductDTO);
    }

    protected ProductDTO mapToProductDTO(Product product) {
        List<ProductImageDTO> imageDTOs = product.getImages().stream()
                .map(image -> new ProductImageDTO(image.getImageUrl()))
                .toList();

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),  // changed to stockQuantity to match your entity field
                product.getNotes(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                imageDTOs
        );
    }
}