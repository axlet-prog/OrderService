package com.example.OrderService.entity;

import jakarta.persistence.*;
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

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    Order order;
}
