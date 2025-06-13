package com.scentpapa.scentpapa_backend.service;

import com.scentpapa.scentpapa_backend.dto.OrderOverviewDTO;
import com.scentpapa.scentpapa_backend.dto.SalesStatsDTO;
import com.scentpapa.scentpapa_backend.dto.TopProductDTO;
import com.scentpapa.scentpapa_backend.dto.UserStatsDTO;

import java.util.List;

public interface AdminStatsService {
    OrderOverviewDTO getOrderOverview();
    SalesStatsDTO getSalesStats(String period);
    List<TopProductDTO> getTopSellingProductsForDays(int days);
    UserStatsDTO getUserStats();
}