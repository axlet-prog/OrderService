package com.example.OrderService.mapper;

import com.example.OrderService.controller.dto.CartItemDto;
import com.example.OrderService.controller.dto.CartResponseDto;

import java.util.List;

public class CartMapper {
    public static CartResponseDto mapListCartItemsDtoToResponse(List<CartItemDto> cartItemDtoList) {
        int totalPrice = 0;
        for (CartItemDto cartItemDto : cartItemDtoList) {
            totalPrice += cartItemDto.getPrice();
        }
        return CartResponseDto.builder()
                .cartItems(cartItemDtoList)
                .totalPrice(totalPrice)
                .build();
    }
}
