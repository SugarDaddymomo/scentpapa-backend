package com.scentpapa.scentpapa_backend.dto;

import com.scentpapa.scentpapa_backend.models.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private String customerEmail;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private List<OrderItemDTO> items;
    private AddressDTO shippingAddress;
    private String razorpayOrderId;
}