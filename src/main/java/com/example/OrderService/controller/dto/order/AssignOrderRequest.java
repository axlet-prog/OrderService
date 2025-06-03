package com.example.OrderService.controller.dto.order;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class AssignOrderRequest {
    private long courierId;
}
