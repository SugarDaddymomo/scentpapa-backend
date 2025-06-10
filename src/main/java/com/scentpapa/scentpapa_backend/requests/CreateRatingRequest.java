package com.scentpapa.scentpapa_backend.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class CreateRatingRequest {
    private Integer rating;
    private String reviewText;
    private MultipartFile reviewImage;
}