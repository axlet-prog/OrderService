package com.example.OrderService.controller.dto.order;

import com.example.OrderService.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OrderListResponse {
    private List<Order> orders;
}
