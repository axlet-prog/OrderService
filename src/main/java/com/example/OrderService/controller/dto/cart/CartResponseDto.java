package com.example.OrderService.controller.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CartResponseDto {
    private List<CartItemDto> cartItems;
    private int totalPrice;
}
