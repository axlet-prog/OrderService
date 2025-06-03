package com.example.OrderService.controller.dto.order;

import com.example.OrderService.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OrderResponse {
    private long id;
    private long customerId;
    private LocalDateTime initDate;
    private OrderStatus status;
    private int totalPrice;
    private List<OrderItemDto> orderItems;
}
