package com.scentpapa.scentpapa_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistDTO {
    private Long productId;
    private String productName;
    private String productDescription;
    private Double productPrice;
}