package com.example.OrderService.controller.dto;

import lombok.Data;

@Data
public class MenuItemInfoResponse {
    private long id;
    private String name;
    private String description;
    private int price;
    private int grams;
}
