package com.scentpapa.scentpapa_backend.service;

import com.scentpapa.scentpapa_backend.dto.OrderDTO;
import com.scentpapa.scentpapa_backend.requests.CreateOrderRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(CreateOrderRequest orderRequest);
    Page<OrderDTO> getOrdersForUser(int page, int size);
    OrderDTO getOrder(Long orderId);
    void cancelOrder(Long orderId);
    OrderDTO updateShippingAddress(Long orderId, Long newAddressId);
}