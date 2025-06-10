package com.scentpapa.scentpapa_backend.service.impl;

import com.scentpapa.scentpapa_backend.models.Product;
import com.scentpapa.scentpapa_backend.models.Rating;
import com.scentpapa.scentpapa_backend.models.User;
import com.scentpapa.scentpapa_backend.repository.ProductRepository;
import com.scentpapa.scentpapa_backend.repository.RatingRepository;
import com.scentpapa.scentpapa_backend.repository.UserRepository;
import com.scentpapa.scentpapa_backend.requests.CreateRatingRequest;
import com.scentpapa.scentpapa_backend.response.RatingResponse;
import com.scentpapa.scentpapa_backend.service.RatingService;
import com.scentpapa.scentpapa_backend.util.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final ProductRepository productRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Override
    public RatingResponse createRating(Long productId, CreateRatingRequest request) throws IOException {
        log.info("Agr yaha tk pahuch rha to auth shi se kaam kr rha hai");
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        User user = getCurrentAuthenticatedUser();
        String imageUrl = null;
        if (request.getReviewImage() != null && !request.getReviewImage().isEmpty()) {
            String contentType = request.getReviewImage().getContentType() != null ? request.getReviewImage().getContentType() : "application/octet-stream";
            imageUrl = s3Service.uploadFile(request.getReviewImage().getBytes(), Objects.requireNonNull(request.getReviewImage().getOriginalFilename()), contentType);
        }

        Rating rating = Rating.builder()
                .product(product)
                .user(user)
                .reviewText(StringUtils.hasText(request.getReviewText()) ? request.getReviewText() : " ")
                .rating(request.getRating())
                .reviewImageUrl(imageUrl)
                .build();

        rating = ratingRepository.save(rating);

        return mapToRatingResponse(rating);
    }

    @Override
    public Page<RatingResponse> getRatings(Long productId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Rating> ratingPage = ratingRepository.findByProductId(productId, pageable);
        List<RatingResponse> dtoResponse = ratingPage.getContent().stream()
                .map(this::mapToRatingResponse)
                .toList();
        return new PageImpl<>(dtoResponse, pageable, ratingPage.getTotalElements());
    }

    private RatingResponse mapToRatingResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .rating(rating.getRating())
                .name(rating.getUser().getFirstName())
                .userId(rating.getUser().getId())
                .createdAt(rating.getCreatedAt())
                .reviewText(rating.getReviewText())
                .reviewImageUrl(rating.getReviewImageUrl())
                .build();
    }

    protected User getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
            User user = (User) principal;
            log.info("EMAIL: {}", user.getEmail());
            return user;
        } else if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername(); // adjust if needed
            log.info("EMAIL from UserDetails: {}", email);
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            throw new RuntimeException("User not found");
        }
    }
}