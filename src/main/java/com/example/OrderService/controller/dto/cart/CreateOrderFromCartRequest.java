package com.example.OrderService.controller.dto.cart;

import lombok.Data;

@Data
public class CreateOrderFromCartRequest {
    private String deliveryAddress;
    //Можно добавть доп инфу и тд.
}
