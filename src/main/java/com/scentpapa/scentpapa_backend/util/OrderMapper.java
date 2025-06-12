package com.scentpapa.scentpapa_backend.util;

import com.scentpapa.scentpapa_backend.dto.OrderDTO;
import com.scentpapa.scentpapa_backend.dto.OrderItemDTO;
import com.scentpapa.scentpapa_backend.dto.UserSummaryDTO;
import com.scentpapa.scentpapa_backend.models.Order;
import com.scentpapa.scentpapa_backend.models.OrderItem;
import com.scentpapa.scentpapa_backend.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final AddressMapper addressMapper;

    public OrderDTO toDto(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .customerEmail(order.getUser().getEmail())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(toOrderItemDtoList(order.getOrderItems()))
                .razorpayOrderId(order.getRazorpayOrderId())
                .shippingAddress(addressMapper.toDto(order.getShippingAddress()))
                .trackingNumber(order.getTrackingNumber())
                .shippingProvider(order.getShippingProvider())
                .expectedDeliveryDate(order.getExpectedDeliveryDate())
                .build();
    }

    public OrderDTO toDtoAdmin(Order order) {
        OrderDTO dto = toDto(order);
        dto.setAdminNotes(StringUtils.hasText(order.getAdminNotes()) ? order.getAdminNotes() : "NA");
        User user = order.getUser();
        if (user != null) {
            UserSummaryDTO userDetails = UserSummaryDTO.builder()
                    .phoneNumber(user.getPhoneNumber())
                    .firstName(user.getFirstName())
                    .lastName(StringUtils.hasText(user.getLastName()) ? user.getLastName() : " ")
                    .id(user.getId())
                    .build();
            dto.setUserDetails(userDetails);
        }
        return dto;
    }

    private List<OrderItemDTO> toOrderItemDtoList(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toOrderItemDto)
                .collect(Collectors.toList());
    }

    private OrderItemDTO toOrderItemDto(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }

    //TODO: to be removed
    public Order toEntity(OrderDTO orderDTO) {
        Order order = new Order();
        order.setId(orderDTO.getId());
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setStatus(orderDTO.getStatus());
        order.setCreatedAt(orderDTO.getCreatedAt());
        order.setUpdatedAt(orderDTO.getUpdatedAt());

        if (orderDTO.getItems() != null) {
            List<OrderItem> orderItems = orderDTO.getItems().stream()
                    .map(itemDTO -> {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setId(itemDTO.getId());
                        orderItem.setQuantity(itemDTO.getQuantity());
                        orderItem.setPrice(itemDTO.getPrice());
                        // Setting product is tricky, because we’d need to fetch it from the DB.
                        // Instead, handle that in the service when persisting.
                        return orderItem;
                    })
                    .collect(Collectors.toList());
            order.setOrderItems(orderItems);
        }

        return order;
    }
}