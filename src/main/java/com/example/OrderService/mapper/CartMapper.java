package com.example.OrderService.mapper;

import com.example.OrderService.controller.dto.cart.CartItemDto;
import com.example.OrderService.controller.dto.cart.CartResponseDto;

import java.util.List;

public class CartMapper {
    public static CartResponseDto mapListCartItemsDtoToResponse(List<CartItemDto> cartItemDtoList) {
        int totalPrice = 0;
        for (CartItemDto cartItemDto : cartItemDtoList) {
            totalPrice += cartItemDto.getPrice() * cartItemDto.getQuantity();
        }
        return CartResponseDto.builder()
                .cartItems(cartItemDtoList)
                .totalPrice(totalPrice)
                .build();
    }
}
