package com.example.OrderService.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderCouriers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    long courierId;

    long orderId;
}
