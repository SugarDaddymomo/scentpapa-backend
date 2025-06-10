package com.scentpapa.scentpapa_backend.service;

import com.scentpapa.scentpapa_backend.requests.CreateRatingRequest;
import com.scentpapa.scentpapa_backend.response.RatingResponse;
import org.springframework.data.domain.Page;

import java.io.IOException;

public interface RatingService {
    RatingResponse createRating(Long productId, CreateRatingRequest request) throws IOException;
    Page<RatingResponse> getRatings(Long productId, int page, int size);
}