package com.example.OrderService.service;

import com.example.OrderService.controller.dto.order.OrderListResponse;
import com.example.OrderService.controller.dto.order.OrderResponse;
import com.example.OrderService.entity.Order;
import com.example.OrderService.entity.OrderItem;
import com.example.OrderService.entity.OrderStatus;
import com.example.OrderService.mapper.OrderMapper;
import com.example.OrderService.repository.OrderItemRepository;
import com.example.OrderService.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(RestTemplate restTemplate, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.restTemplate = restTemplate;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public OrderListResponse getAllOrders(String bearerToken, Set<OrderStatus> statuses) {
        Specification<Order> orderSpecification = OrderSpecification.hasStatusIn(statuses);


        List<Order> orderList = orderRepository.findAll(orderSpecification);
        return new OrderListResponse(orderList);
    }

    public OrderResponse getOrderById(long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrder(order).orElseThrow(() -> new RuntimeException("Order not found"));

        return OrderMapper.mapToOrderResponse(order, orderItemList);
    }

    public void assignOrder(long orderId, long courierId) {

    }
}
