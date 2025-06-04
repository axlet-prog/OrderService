package com.example.OrderService.repository;

import com.example.OrderService.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Optional<List<Order>> findOrdersByUserId(Long userId);
    List<Order> findAllByUserId(Long userId);
}
