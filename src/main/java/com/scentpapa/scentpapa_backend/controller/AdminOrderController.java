package com.scentpapa.scentpapa_backend.controller;

import com.scentpapa.scentpapa_backend.dto.AdminCancelOrderDTO;
import com.scentpapa.scentpapa_backend.dto.AdminUpdateOrderRequest;
import com.scentpapa.scentpapa_backend.dto.OrderDTO;
import com.scentpapa.scentpapa_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scentpapa/v1/admin/order")
@RequiredArgsConstructor
@Slf4j
public class AdminOrderController {

    private final OrderService orderService;

    //TODO: will be done later
//    @PostMapping
//    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
//        Order order = orderService.createOrderAsAdmin(orderDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toDto(order));
//    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDTO>> listOrders(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        Page<OrderDTO> customerOrders = orderService.getCustomerOrders(pageable, status, customerEmail, startDate, endDate);
        return ResponseEntity.ok(customerOrders);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderForAdmin(orderId);
        return ResponseEntity.ok(orderDTO);
    }

    @PutMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long orderId, @RequestBody AdminUpdateOrderRequest updateOrderRequest) {
        OrderDTO order = orderService.updateOrderByAdmin(orderId, updateOrderRequest);
        return ResponseEntity.ok(order);
    }

    //TODO: will be done later if needed
//    @PutMapping("/{orderId}/cancel")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> cancelAdminOrder(@PathVariable Long orderId, @RequestBody AdminCancelOrderDTO cancelOrderDTO) {
//
//    }
}