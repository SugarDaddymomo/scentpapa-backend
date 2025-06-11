package com.scentpapa.scentpapa_backend.controller;

import com.scentpapa.scentpapa_backend.dto.OrderDTO;
import com.scentpapa.scentpapa_backend.requests.CreateOrderRequest;
import com.scentpapa.scentpapa_backend.requests.PaymentVerificationRequest;
import com.scentpapa.scentpapa_backend.requests.UpdateShippingAddressRequest;
import com.scentpapa.scentpapa_backend.service.OrderService;
import com.scentpapa.scentpapa_backend.util.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scentpapa/v1/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final RazorpayService razorpayService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest orderRequest) {
        log.info("Request to place order: {}", orderRequest);
        OrderDTO orderDTO = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(orderDTO);
    }

    @PostMapping("/confirm-payment")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentVerificationRequest paymentVerificationRequest) {
        log.info("verifying payment: {}", paymentVerificationRequest);
        return ResponseEntity.ok(razorpayService.verifyPayment(paymentVerificationRequest));
    }

    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderDTO>> getUserOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<OrderDTO> orders = orderService.getOrdersForUser(page, size);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderDTO> getSingleUser(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @PatchMapping("/{orderId}/address")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderDTO> updateShippingAddress(@PathVariable Long orderId, @RequestBody UpdateShippingAddressRequest addressRequest) {
        OrderDTO order = orderService.updateShippingAddress(orderId, addressRequest.getNewAddressId());
        return ResponseEntity.ok(order);
    }
}