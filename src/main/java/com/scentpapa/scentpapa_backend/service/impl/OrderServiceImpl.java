package com.scentpapa.scentpapa_backend.service.impl;

import com.scentpapa.scentpapa_backend.dto.AdminUpdateOrderRequest;
import com.scentpapa.scentpapa_backend.dto.OrderDTO;
import com.scentpapa.scentpapa_backend.models.*;
import com.scentpapa.scentpapa_backend.repository.*;
import com.scentpapa.scentpapa_backend.requests.CreateOrderRequest;
import com.scentpapa.scentpapa_backend.requests.OrderItemRequest;
import com.scentpapa.scentpapa_backend.service.CartService;
import com.scentpapa.scentpapa_backend.service.OrderService;
import com.scentpapa.scentpapa_backend.service.WishlistService;
import com.scentpapa.scentpapa_backend.util.InvoiceService;
import com.scentpapa.scentpapa_backend.util.OrderMapper;
import com.scentpapa.scentpapa_backend.util.RazorpayService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    private final CartService cartService;
    private final InvoiceService invoiceService;
    private final WishlistService wishlistService;

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
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);
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
        savedOrder.setShippingProvider("NA");
        savedOrder.setTrackingNumber("NA");
        savedOrder.setExpectedDeliveryDate(Instant.now().plus(5, ChronoUnit.DAYS));
        orderRepository.save(savedOrder);
        razorpayService.createRazorpayOrder(savedOrder);
        cartService.clearUserCart();
        for (OrderItem item : savedOrder.getOrderItems()) {
            wishlistService.removeFromWishList(item.getProduct().getId());
        }
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

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            // Save updated stock
            productRepository.save(product);
        }

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

    @Override
    public Page<OrderDTO> getCustomerOrders(Pageable pageable, String status, String customerEmail, String startDate, String endDate) {
        Page<Order> orderPage = orderRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), OrderStatus.valueOf(status.toUpperCase())));
            }

            if (customerEmail != null && !customerEmail.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("user").get("email")), "%" + customerEmail.toLowerCase() + "%"));
            }

            if (startDate != null && endDate != null) {
                Instant start = Instant.parse(startDate);
                Instant end = Instant.parse(endDate);
                predicates.add(cb.between(root.get("createdAt"), start, end));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
        return orderPage.map(orderMapper::toDto);
    }

    @Override
    public OrderDTO getOrderForAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
        return orderMapper.toDtoAdmin(order);
    }

    @Override
    public OrderDTO updateOrderByAdmin(Long orderId, AdminUpdateOrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }
        if (StringUtils.hasText(request.getTrackingNumber())) {
            order.setTrackingNumber(request.getTrackingNumber());
        }
        if (StringUtils.hasText(request.getShippingProvider())) {
            order.setShippingProvider(request.getShippingProvider());
        }
        if (StringUtils.hasText(request.getAdminNotes())) {
            order.setAdminNotes(request.getAdminNotes());
        }
        if (request.getExpectedDeliveryDate() != null) {
            order.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        }
        order = orderRepository.save(order);
        return orderMapper.toDtoAdmin(order);
    }

    @Override
    public ResponseEntity<byte[]> generateInvoiceResponse(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        ByteArrayInputStream pdfStream = invoiceService.generateInvoice(order);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=invoice_" + order.getReferenceNumber() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }
}