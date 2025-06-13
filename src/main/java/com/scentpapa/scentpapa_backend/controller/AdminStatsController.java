package com.scentpapa.scentpapa_backend.controller;

import com.scentpapa.scentpapa_backend.dto.OrderOverviewDTO;
import com.scentpapa.scentpapa_backend.dto.SalesStatsDTO;
import com.scentpapa.scentpapa_backend.dto.TopProductDTO;
import com.scentpapa.scentpapa_backend.dto.UserStatsDTO;
import com.scentpapa.scentpapa_backend.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/scentpapa/v1/admin/stats")
@RequiredArgsConstructor
@Slf4j
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderOverviewDTO> getOrderReview() {
        return ResponseEntity.ok(adminStatsService.getOrderOverview());
    }

    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalesStatsDTO> getSalesStats(@RequestParam(defaultValue = "monthly") String period) {
        return ResponseEntity.ok(adminStatsService.getSalesStats(period));
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TopProductDTO>> getTopSellingProducts(@RequestParam(defaultValue = "30") int days) {
        List<TopProductDTO> topProducts = adminStatsService.getTopSellingProductsForDays(days);
        return ResponseEntity.ok(topProducts);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserStats() {
        UserStatsDTO userStats = adminStatsService.getUserStats();
        return ResponseEntity.ok(userStats);
    }
}