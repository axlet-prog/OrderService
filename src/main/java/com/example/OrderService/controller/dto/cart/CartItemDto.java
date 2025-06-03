package com.example.OrderService.controller.dto.cart;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CartItemDto {
    private long itemId;
    private String title;
    private int price;
    private int quantity;
    private int grams;
}
