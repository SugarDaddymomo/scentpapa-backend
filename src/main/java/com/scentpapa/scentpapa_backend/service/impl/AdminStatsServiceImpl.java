package com.scentpapa.scentpapa_backend.service.impl;

import com.scentpapa.scentpapa_backend.dto.*;
import com.scentpapa.scentpapa_backend.models.Order;
import com.scentpapa.scentpapa_backend.models.OrderStatus;
import com.scentpapa.scentpapa_backend.repository.OrderRepository;
import com.scentpapa.scentpapa_backend.repository.UserRepository;
import com.scentpapa.scentpapa_backend.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminStatsServiceImpl implements AdminStatsService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public OrderOverviewDTO getOrderOverview() {
        Long totalOrders = orderRepository.count();
        // Orders grouped by status
        List<Object[]> groupedOrders = orderRepository.countOrdersByStatus();
        Map<String, Long> ordersByStatus = new HashMap<>();
        for (Object[] row : groupedOrders) {
            ordersByStatus.put(((OrderStatus) row[0]).name(), (Long) row[1]);
        }

        // Recent 10 orders
        List<Order> recentOrders = orderRepository.findTop10ByOrderByCreatedAtDesc();
        List<RecentOrderDTO> recentOrderDTOs = recentOrders.stream()
                .map(order -> RecentOrderDTO.builder()
                        .orderId(order.getId())
                        .customerName(order.getUser().getFirstName())
                        .status(order.getStatus().name())
                        .totalAmount(order.getTotalAmount())
                        .createdAt(order.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return OrderOverviewDTO.builder()
                .totalOrders(totalOrders)
                .ordersByStatus(ordersByStatus)
                .recentOrders(recentOrderDTOs)
                .build();
    }

    @Override
    public SalesStatsDTO getSalesStats(String period) {
        Instant now = Instant.now();
        Instant startDate;
        switch (period.toLowerCase()) {
            case "daily":
                startDate = now.minus(1, ChronoUnit.DAYS);
                break;
            case "weekly":
                startDate = now.minus(7, ChronoUnit.DAYS);
                break;
            case "monthly":
            default:
                startDate = now.minus(30, ChronoUnit.DAYS);
                break;
        }

        // Step 1: Fetch delivered/completed orders in the time window
        List<Order> orders = orderRepository.findByCreatedAtBetweenAndStatus(startDate, now, OrderStatus.DELIVERED);

        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long totalOrders = (long) orders.size();

        BigDecimal averageOrderValue = totalOrders == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);

        // Step 2: Count total users in the system
        Long totalUsers = userRepository.countCustomers();

        // Step 3: Orders by status (for full system, not just in period)
        List<Object[]> statusCounts = orderRepository.countOrdersByStatus();
        Map<String, Long> orderStatusCounts = new HashMap<>();
        for (Object[] row : statusCounts) {
            orderStatusCounts.put(((OrderStatus) row[0]).name(), (Long) row[1]);
        }

        // Step 4: Top-selling products
        List<TopProductDTO> topProducts = orderRepository.findTopSellingProducts(startDate, now);

        // Step 5: Revenue grouped by period
        List<RevenueByMonthDTO> revenueByPeriod;
        if ("daily".equalsIgnoreCase(period)) {
            List<Object[]> rawRevenue = orderRepository.aggregateRevenueByDayRaw(startDate, now);
            revenueByPeriod = rawRevenue.stream()
                    .map(row -> new RevenueByMonthDTO((String) row[0], (BigDecimal) row[1]))
                    .collect(Collectors.toList());
        } else {
            List<Object[]> rawRevenue = orderRepository.aggregateRevenueByMonthRaw(startDate, now);
            revenueByPeriod = rawRevenue.stream()
                    .map(row -> new RevenueByMonthDTO((String) row[0], (BigDecimal) row[1]))
                    .collect(Collectors.toList());
        }
        return SalesStatsDTO.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .averageOrderValue(averageOrderValue)
                .totalUsers(totalUsers)
                .orderStatusCounts(orderStatusCounts)
                .topSellingProducts(topProducts)
                .revenueByMonth(revenueByPeriod)
                .build();
    }

    @Override
    public List<TopProductDTO> getTopSellingProductsForDays(int days) {
        Instant now = Instant.now();
        Instant start = now.minus(days, ChronoUnit.DAYS);
        return orderRepository.findTopSellingProducts(start, now);
    }

    @Override
    public UserStatsDTO getUserStats() {
        Instant now = Instant.now();
        Instant weekAgo = now.minus(7, ChronoUnit.DAYS);
        Instant monthAgo = now.minus(30, ChronoUnit.DAYS);

        Long totalCustomers = userRepository.countCustomers();
        Long totalAdmins = userRepository.countAdmins();
        Long newCustomersThisWeek = userRepository.countNewCustomersBetween(weekAgo, now);
        Long newCustomersThisMonth = userRepository.countNewCustomersBetween(monthAgo, now);
        Long returningCustomers = userRepository.countReturningCustomers();
        Long newCustomersWithoutOrders = userRepository.countNewCustomersWithoutOrders();

        return UserStatsDTO.builder()
                .totalCustomers(totalCustomers)
                .totalAdmins(totalAdmins)
                .newCustomersThisWeek(newCustomersThisWeek)
                .newCustomersThisMonth(newCustomersThisMonth)
                .returningCustomers(returningCustomers)
                .newCustomersWithoutOrders(newCustomersWithoutOrders)
                .build();
    }
}