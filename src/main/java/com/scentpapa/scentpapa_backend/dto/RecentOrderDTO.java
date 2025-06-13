package com.scentpapa.scentpapa_backend.dto;

import com.scentpapa.scentpapa_backend.models.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentOrderDTO {
    private Long orderId;
    private String customerName;
    private String status;
    private BigDecimal totalAmount;
    private Instant createdAt;
}