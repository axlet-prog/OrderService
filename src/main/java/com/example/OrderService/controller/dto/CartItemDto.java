package com.example.OrderService.controller.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CartItemDto {
    String name;
    int price;
    int quantity;
    int grams;
}
