package com.example.OrderService.service;

import com.example.OrderService.controller.dto.order.OrderListResponse;
import com.example.OrderService.controller.dto.order.OrderResponse;
import com.example.OrderService.entity.Order;
import com.example.OrderService.entity.OrderCouriers;
import com.example.OrderService.entity.OrderItem;
import com.example.OrderService.entity.OrderStatus;
import com.example.OrderService.mapper.OrderMapper;
import com.example.OrderService.repository.OrderCourierRepository;
import com.example.OrderService.repository.OrderItemRepository;
import com.example.OrderService.repository.OrderRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private final NetworkService networkService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderCourierRepository orderCourierRepository;

    public OrderService(NetworkService networkService, OrderRepository orderRepository, OrderItemRepository orderItemRepository, OrderCourierRepository orderCourierRepository) {
        this.networkService = networkService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderCourierRepository = orderCourierRepository;
    }

    public OrderListResponse getAllOrders(String bearerToken, Set<OrderStatus> statuses) {
        String role = networkService.getUserRoleByToken(bearerToken);
        Specification<Order> orderSpecification = OrderSpecification.hasStatusIn(statuses);

        List<Order> orderList;

        switch (role) {
            case "ROLE_ADMIN" -> orderList = orderRepository.findAll(orderSpecification);
            case "ROLE_CLIENT" -> {
                long userId = networkService.getIdFromToken(bearerToken);
                orderList = orderRepository.findOrdersByUserId(userId).orElse(new ArrayList<>());
            }
            case "ROLE_COURIER" -> orderList = new ArrayList<>();

            default -> throw new RuntimeException("Invalid role");
        }

        return new OrderListResponse(orderList);
    }

    public OrderResponse getOrderById(String bearerToken, long id) {
        String userRole = networkService.getUserRoleByToken(bearerToken);
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        switch (userRole) {
            case "ROLE_CLIENT" -> {
                long userId = networkService.getIdFromToken(bearerToken);
                if (order.getUserId() != userId) {
                    throw new RuntimeException("Invalid user id");
                }
            }
            case "ROLE_ADMIN" -> {
            }
            case "ROLE_COURIER" -> {

            }
            default -> throw new RuntimeException("Invalid role");
        }

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrder(order).orElseThrow(() -> new RuntimeException("Order not found"));

        return OrderMapper.mapToOrderResponse(order, orderItemList);
    }

    public void assignOrder(long orderId, long courierId) {
        String role = networkService.getUserRoleById(courierId);
        if (!role.equals("ROLE_COURIER")) {
            throw new RuntimeException("Invalid role for assign order");
        }

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        orderCourierRepository.save(
                OrderCouriers.builder()
                        .order(order)
                        .courierId(courierId)
                        .build()
        );

        order.setOrderStatus(OrderStatus.IN_PROGRESS);
        orderRepository.save(
                order
        );
    }
}
