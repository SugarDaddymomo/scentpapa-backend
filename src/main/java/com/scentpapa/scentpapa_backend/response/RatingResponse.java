package com.scentpapa.scentpapa_backend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingResponse {
    private Long id;
    private Long userId;
    private String name;
    private Integer rating;
    private String reviewText;
    private String reviewImageUrl;
    private Instant createdAt;
}