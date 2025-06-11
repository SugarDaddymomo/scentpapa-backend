package com.scentpapa.scentpapa_backend.service.impl;

import com.scentpapa.scentpapa_backend.dto.OrderDTO;
import com.scentpapa.scentpapa_backend.models.*;
import com.scentpapa.scentpapa_backend.repository.AddressRepository;
import com.scentpapa.scentpapa_backend.repository.OrderRepository;
import com.scentpapa.scentpapa_backend.repository.PaymentRepository;
import com.scentpapa.scentpapa_backend.repository.ProductRepository;
import com.scentpapa.scentpapa_backend.requests.CreateOrderRequest;
import com.scentpapa.scentpapa_backend.requests.OrderItemRequest;
import com.scentpapa.scentpapa_backend.service.OrderService;
import com.scentpapa.scentpapa_backend.util.OrderMapper;
import com.scentpapa.scentpapa_backend.util.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final RatingServiceImpl ratingService;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final RazorpayService razorpayService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest orderRequest) {
        log.info("starting to create the order");
        User user = ratingService.getCurrentAuthenticatedUser();
        Address shippingAddress = addressRepository.findById(orderRequest.getShippingAddressId())
                .orElseThrow(() -> new RuntimeException("Shipping Address not found!"));

        if (!shippingAddress.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to shipping address!");
        }
        log.info("main order flow shuru");
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.getProductId()));
            BigDecimal productPrice = BigDecimal.valueOf(product.getPrice());
            BigDecimal itemTotal = productPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(productPrice)
                    .build();

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setShippingAddress(shippingAddress);

        Order savedOrder = orderRepository.save(order);
        savedOrder.setReferenceNumber("SP-"+savedOrder.getId());
        orderRepository.save(savedOrder);
        razorpayService.createRazorpayOrder(savedOrder);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public Page<OrderDTO> getOrdersForUser(int page, int size) {
        User user = ratingService.getCurrentAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders = orderRepository.findAllByUser(user, pageable);
        return orders.map(orderMapper::toDto);
    }

    @Override
    public OrderDTO getOrder(Long orderId) {
        User user = ratingService.getCurrentAuthenticatedUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order cannot be cancelled at this stage");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Payment record not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS && StringUtils.hasText(payment.getRazorpayPaymentId())) {
            try {
                razorpayService.refund(payment, payment.getAmount());
            } catch (Exception e) {
                log.error("Error while refunding the amount to source! ", e);
            }
        }
    }

    @Override
    public OrderDTO updateShippingAddress(Long orderId, Long newAddressId) {
        User user = ratingService.getCurrentAuthenticatedUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to order!");
        }

        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot change address once the order is shipped or delivered or cancelled!");
        }

        Address newAddress = addressRepository.findById(newAddressId)
                .orElseThrow(() -> new RuntimeException("Address not found!"));

        if (!newAddress.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to address!");
        }

        order.setShippingAddress(newAddress);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }
}