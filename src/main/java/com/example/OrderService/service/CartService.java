package com.example.OrderService.service;

import com.example.OrderService.controller.dto.cart.CartItemDto;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestTemplate restTemplate;
    private final CartRepository cartRepository;

    @Value("${services.restaurant-service.url}")
    private String restaurantServiceUrl;
    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    public CartService(RestTemplate restTemplate, CartRepository cartRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.restTemplate = restTemplate;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public CartItem addCardItem(long itemId, int quantity, String bearerToken) {
        getInfo(itemId);
        long userId = getIdFromToken(bearerToken);
        Optional<CartItem> cartItem = cartRepository.findCartItemByUserIdAndItemId(userId,itemId);
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
        long userId = getIdFromToken(bearerToken);

        Optional<List<CartItem>> cartItems = cartRepository.findAllByUserId(userId);
        if (cartItems.isEmpty()) {
            return new ArrayList<>();
        }
        return cartItems.get().stream()
                .map(cartItem -> {
                    MenuItemInfoResponse menuItemInfo = getInfo(cartItem.getItemId());
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
        long userId = getIdFromToken(bearerToken);
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

    public OrderResponse createOrderFromCart(String bearerToken) {
        long userId = getIdFromToken(bearerToken);
        List<CartItem> cartItems = cartRepository.findAllByUserId(userId).orElseThrow(
                () -> new RuntimeException("Cannot find cart for user " + userId)
        );

        Order order = Order.builder()
                .userId(userId)
                .orderInitDate(LocalDateTime.now())
                .orderFinishDate(null)
                .orderStatus(OrderStatus.INIT)
                .build();

        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();

        cartItems.forEach(cartItem -> {
            var info = getInfo(cartItem.getItemId());
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
            orderItems.add(orderItem);
        });

        var response = OrderMapper.mapToOrderResponse(
                order, orderItems
        );

        cartRepository.deleteAllByUserId(userId);
        return response;
    }

    private MenuItemInfoResponse getInfo(long itemId) {
        String getItemUrl = restaurantServiceUrl + "/menu/" + itemId;
        ResponseEntity<MenuItemInfoResponse> checkResponse = restTemplate.getForEntity(getItemUrl, MenuItemInfoResponse.class);
        if (checkResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("failed to get item with id " + itemId);
        }
        return checkResponse.getBody();
    }

    private long getIdFromToken(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);

        String parseIdUrl = authServiceUrl + "/auth/parse_id";
        ResponseEntity<Long> parseIdResponse = restTemplate.postForEntity(parseIdUrl, entity, Long.class);
        if (parseIdResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Check jwt failed");
        }

        return parseIdResponse.getBody();
    }
}