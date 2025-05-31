package com.example.OrderService.controller.dto;

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

    long id;

    LocalDateTime initDate;

    OrderStatus status;

    int totalPrice;

    List<OrderItemDto> orderItems;
}
