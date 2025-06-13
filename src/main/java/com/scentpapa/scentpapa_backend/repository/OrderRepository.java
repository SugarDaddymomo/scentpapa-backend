package com.scentpapa.scentpapa_backend.repository;

import com.scentpapa.scentpapa_backend.dto.RevenueByMonthDTO;
import com.scentpapa.scentpapa_backend.dto.TopProductDTO;
import com.scentpapa.scentpapa_backend.models.Order;
import com.scentpapa.scentpapa_backend.models.OrderStatus;
import com.scentpapa.scentpapa_backend.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByRazorpayOrderId(String razorpayId);
    Page<Order> findAllByUser(User user, Pageable pageable);
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();
    List<Order> findTop10ByOrderByCreatedAtDesc();
    List<Order> findByCreatedAtBetweenAndStatus(Instant start, Instant end, OrderStatus status);

    @Query("SELECT new com.scentpapa.scentpapa_backend.dto.TopProductDTO(" +
            "oi.product.name, SUM(oi.quantity), SUM(oi.price * oi.quantity)) " +
            "FROM OrderItem oi " +
            "WHERE oi.order.createdAt BETWEEN :start AND :end AND oi.order.status = 'DELIVERED' " +
            "GROUP BY oi.product.name " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<TopProductDTO> findTopSellingProducts(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM'), SUM(o.totalAmount) " +
            "FROM Order o WHERE o.createdAt BETWEEN :start AND :end AND o.status = 'DELIVERED' " +
            "GROUP BY FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM') " +
            "ORDER BY FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM')")
    List<Object[]> aggregateRevenueByMonthRaw(@Param("start") Instant start, @Param("end") Instant end);


    @Query("SELECT FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM-DD'), SUM(o.totalAmount) " +
            "FROM Order o WHERE o.createdAt BETWEEN :start AND :end AND o.status = 'DELIVERED' " +
            "GROUP BY FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM-DD') " +
            "ORDER BY FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM-DD')")
    List<Object[]> aggregateRevenueByDayRaw(@Param("start") Instant start, @Param("end") Instant end);


}