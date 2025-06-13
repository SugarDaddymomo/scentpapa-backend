package com.scentpapa.scentpapa_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderOverviewDTO {
    private Long totalOrders;
    private Map<String, Long> ordersByStatus;
    private List<RecentOrderDTO> recentOrders;
}