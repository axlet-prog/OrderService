package com.example.OrderService.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemDto {
    private String name;
    private String description;
    private int price;
    private int grams;
    private int quantity;
}
