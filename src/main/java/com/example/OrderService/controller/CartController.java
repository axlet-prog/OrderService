package com.example.OrderService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/cart")
public class CartController {

    @GetMapping
    public String getCart() {

    }
}
