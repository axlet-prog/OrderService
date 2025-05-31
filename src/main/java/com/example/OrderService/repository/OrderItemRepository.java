package com.example.OrderService.repository;

import com.example.OrderService.entity.Order;
import com.example.OrderService.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<List<OrderItem>> findAllByOrder(Order order);
}
