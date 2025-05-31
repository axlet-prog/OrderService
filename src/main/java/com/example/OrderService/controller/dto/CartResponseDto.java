package com.example.OrderService.controller.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class CartResponseDto {
    List<CartItemDto> cartItems;
    int totalPrice;
}
