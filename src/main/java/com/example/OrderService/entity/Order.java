package com.example.OrderService.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long userId;

    private LocalDateTime orderInitDate;

    @Nullable
    private LocalDateTime orderFinishDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String deliveryAddress;
}
