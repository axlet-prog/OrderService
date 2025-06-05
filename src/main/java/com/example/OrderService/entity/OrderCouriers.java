package com.example.OrderService.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Builder
@Getter
@Setter
@AllArgsConstructor
public class OrderCouriers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long courierId;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
}
