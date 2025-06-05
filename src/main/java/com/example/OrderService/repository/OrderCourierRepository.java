package com.example.OrderService.repository;

import com.example.OrderService.entity.OrderCouriers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderCourierRepository extends JpaRepository<OrderCouriers, Long> {
}
