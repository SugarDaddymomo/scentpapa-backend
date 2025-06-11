package com.scentpapa.scentpapa_backend.requests;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderItemRequest {
    private Long productId;
    private int quantity;
}