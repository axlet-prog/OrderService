package com.example.OrderService.repository;

import com.example.OrderService.entity.OrderCouriers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderCourierRepository extends JpaRepository<OrderCouriers, Long> {
    Optional<List<OrderCouriers>> findAllByCourierId(Long courierId);
}
