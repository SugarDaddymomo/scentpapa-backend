package com.scentpapa.scentpapa_backend.service;

import com.scentpapa.scentpapa_backend.dto.AdminUpdateOrderRequest;
import com.scentpapa.scentpapa_backend.dto.OrderDTO;
import com.scentpapa.scentpapa_backend.requests.CreateOrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;


public interface OrderService {
    OrderDTO createOrder(CreateOrderRequest orderRequest);
    Page<OrderDTO> getOrdersForUser(int page, int size);
    OrderDTO getOrder(Long orderId);
    void cancelOrder(Long orderId);
    OrderDTO updateShippingAddress(Long orderId, Long newAddressId);
    Page<OrderDTO> getCustomerOrders(Pageable pageable, String status, String customerEmail, String startDate, String endDate);
    OrderDTO getOrderForAdmin(Long orderId);
    OrderDTO updateOrderByAdmin(Long orderId, AdminUpdateOrderRequest request);
    ResponseEntity<byte[]> generateInvoiceResponse(Long orderId);
}