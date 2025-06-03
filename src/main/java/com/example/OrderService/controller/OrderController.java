package com.example.OrderService.controller;

import com.example.OrderService.controller.dto.order.AssignOrderRequest;
import com.example.OrderService.controller.dto.order.OrderListResponse;
import com.example.OrderService.controller.dto.order.OrderResponse;
import com.example.OrderService.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/orders")
@RestController
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<OrderListResponse> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderInfo(@PathVariable int id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

//    @PostMapping("/{id}/assign")
//    public ResponseEntity<Void> assignToCourier(@PathVariable long id, @RequestBody AssignOrderRequest request) {
//
//
//    }
}
