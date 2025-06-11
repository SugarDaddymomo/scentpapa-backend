package com.scentpapa.scentpapa_backend.requests;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateOrderRequest {
    private List<OrderItemRequest> items;
    private Long shippingAddressId;
}