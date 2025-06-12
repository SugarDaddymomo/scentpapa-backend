package com.scentpapa.scentpapa_backend.dto;

import com.scentpapa.scentpapa_backend.models.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUpdateOrderRequest {
    private OrderStatus status;
    private String trackingNumber;
    private String shippingProvider;
    private String adminNotes;
    private Instant expectedDeliveryDate;
}