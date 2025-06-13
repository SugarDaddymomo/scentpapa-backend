package com.scentpapa.scentpapa_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesStatsDTO {
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long totalUsers;
    private BigDecimal averageOrderValue;
    private Map<String, Long> orderStatusCounts;
    private List<RevenueByMonthDTO> revenueByMonth;
    private List<TopProductDTO> topSellingProducts;
}