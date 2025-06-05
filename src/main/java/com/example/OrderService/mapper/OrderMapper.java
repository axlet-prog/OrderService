package com.example.OrderService.mapper;

import com.example.OrderService.controller.dto.order.OrderItemDto;
import com.example.OrderService.controller.dto.order.OrderResponse;
import com.example.OrderService.entity.Order;
import com.example.OrderService.entity.OrderItem;

import java.util.List;

public class OrderMapper {
    public static OrderItemDto mapOrderItemEntityToDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getItemName(),
                orderItem.getItemDescription(),
                orderItem.getUnitPrice(),
                orderItem.getUnitGrams(),
                orderItem.getQuantity()
        );
    }

    public static OrderResponse mapToOrderResponse(Order order, List<OrderItem> orderItems) {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getUnitPrice() * orderItem.getQuantity();
        }

        List<OrderItemDto> orderItemDtos = orderItems.stream()
                .map(OrderMapper::mapOrderItemEntityToDto)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getDeliveryAddress(),
                order.getOrderInitDate(),
                order.getOrderStatus(),
                totalPrice,
                orderItemDtos
        );
    }
}
