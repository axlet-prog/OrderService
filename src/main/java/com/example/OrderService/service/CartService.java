package com.example.OrderService.service;

import com.example.OrderService.controller.dto.cart.CartItemDto;
import com.example.OrderService.controller.dto.cart.CreateOrderFromCartRequest;
import com.example.OrderService.controller.dto.cart.MenuItemInfoResponse;
import com.example.OrderService.controller.dto.order.OrderResponse;
import com.example.OrderService.entity.CartItem;
import com.example.OrderService.entity.Order;
import com.example.OrderService.entity.OrderItem;
import com.example.OrderService.entity.OrderStatus;
import com.example.OrderService.mapper.OrderMapper;
import com.example.OrderService.repository.CartRepository;
import com.example.OrderService.repository.OrderItemRepository;
import com.example.OrderService.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final NetworkService networkService;


    public CartService(RestTemplate restTemplate, CartRepository cartRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, NetworkService networkService) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.networkService = networkService;
    }

    public CartItem addCardItem(long itemId, int quantity, String bearerToken) {
        networkService.getInfo(itemId);
        long userId = networkService.getIdFromToken(bearerToken);
        Optional<CartItem> cartItem = cartRepository.findCartItemByUserIdAndItemId(userId, itemId);
        if (cartItem.isPresent()) {
            cartItem.get().setQuantity(cartItem.get().getQuantity() + quantity);
            return cartRepository.save(cartItem.get());
        } else {
            return cartRepository.save(CartItem.builder()
                    .itemId(itemId)
                    .userId(userId)
                    .quantity(quantity)
                    .build());
        }
    }

    public List<CartItemDto> getCartItems(String bearerToken) {
        long userId = networkService.getIdFromToken(bearerToken);

        Optional<List<CartItem>> cartItems = cartRepository.findAllByUserId(userId);
        if (cartItems.isEmpty()) {
            return new ArrayList<>();
        }
        return cartItems.get().stream()
                .map(cartItem -> {
                    MenuItemInfoResponse menuItemInfo = networkService.getInfo(cartItem.getItemId());
                    return CartItemDto.builder()
                            .itemId(cartItem.getItemId())
                            .title(menuItemInfo.getTitle())
                            .price(menuItemInfo.getPrice())
                            .quantity(cartItem.getQuantity())
                            .grams(menuItemInfo.getGrams())
                            .build();
                }).toList();
    }

    public void removeCartItem(long itemId, int quantity, String bearerToken) {
        long userId = networkService.getIdFromToken(bearerToken);
        CartItem cartItem = cartRepository.findCartItemByUserIdAndItemId(userId, itemId).orElseThrow(
                () -> new RuntimeException("Cannot find cart item with id " + itemId + " for user " + userId)
        );
        if (cartItem.getQuantity() > quantity) {
            cartItem.setQuantity(cartItem.getQuantity() - quantity);
            cartRepository.save(cartItem);
        } else {
            cartRepository.delete(cartItem);
        }
    }

    @Transactional
    public OrderResponse createOrderFromCart(String bearerToken, CreateOrderFromCartRequest request) {
        long userId = networkService.getIdFromToken(bearerToken);
        List<CartItem> cartItems = cartRepository.findAllByUserId(userId).orElseThrow(
                () -> new RuntimeException("Cannot find cart for user " + userId)
        );

        if (cartItems.isEmpty()) {
            throw new RuntimeException("В корзине должна быть хотя бы 1 позиция");
        }

        Order order = Order.builder()
                .userId(userId)
                .orderInitDate(LocalDateTime.now())
                .orderFinishDate(null)
                .deliveryAddress(request.getDeliveryAddress())
                .orderStatus(OrderStatus.INIT)
                .build();

        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();

        cartItems.forEach(cartItem -> {
            var info = networkService.getInfo(cartItem.getItemId());
            var orderItem = orderItemRepository.save(
                    OrderItem.builder()
                            .order(order)
                            .itemName(info.getTitle())
                            .itemDescription(info.getDescription())
                            .unitPrice(info.getPrice())
                            .unitGrams(info.getGrams())
                            .quantity(cartItem.getQuantity())
                            .build()
            );
            cartRepository.delete(cartItem);
            orderItems.add(orderItem);
        });

        return OrderMapper.mapToOrderResponse(
                order, orderItems
        );
    }
}