package com.example.OrderService.service;


import com.example.OrderService.entity.Order;
import com.example.OrderService.entity.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;


public class OrderSpecification {
    public static Specification<Order> hasStatusIn(Set<OrderStatus> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return criteriaBuilder.conjunction(); // No filtering if no statuses are provided
            }
            return root.get("orderStatus").in(statuses);
        };
    }

}
