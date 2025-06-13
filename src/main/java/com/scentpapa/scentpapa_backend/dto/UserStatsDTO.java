package com.scentpapa.scentpapa_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatsDTO {
    private Long totalCustomers;
    private Long totalAdmins;
    private Long newCustomersThisWeek;
    private Long newCustomersThisMonth;
    private Long returningCustomers;
    private Long newCustomersWithoutOrders;
}