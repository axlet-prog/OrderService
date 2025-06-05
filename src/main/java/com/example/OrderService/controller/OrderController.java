package com.example.OrderService.controller;

import com.example.OrderService.controller.dto.order.AssignOrderRequest;
import com.example.OrderService.controller.dto.order.OrderListResponse;
import com.example.OrderService.controller.dto.order.OrderResponse;
import com.example.OrderService.entity.OrderStatus;
import com.example.OrderService.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/orders")
@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<OrderListResponse> getAllOrders(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam(name = "statuses", required = false) Set<OrderStatus> statuses
    ) {
        return ResponseEntity.ok(orderService.getAllOrders(bearerToken, statuses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderInfo(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable int id) {
        return ResponseEntity.ok(orderService.getOrderById(bearerToken, id));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<Void> assignToCourier(@PathVariable long id, @RequestBody AssignOrderRequest request) {
        orderService.assignOrder(id, request.getCourierId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/set_status")
    public ResponseEntity<Void> setOrderStatus(@PathVariable("id") long orderId, @RequestParam("status") OrderStatus status) {
        orderService.changeOrderStatus(orderId, status);
        return ResponseEntity.ok().build();
    }
}
