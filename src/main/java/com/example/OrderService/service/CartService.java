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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class CartService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final NetworkService networkService;

    public CartItem addCardItem(long itemId, int quantity, String bearerToken) {
        log.info("Attempting to add item ID: {} with quantity: {} to cart.", itemId, quantity);
        networkService.getInfo(itemId); // Verifies item exists
        long userId = networkService.getIdFromToken(bearerToken);
        log.debug("User ID {} extracted from token.", userId);

        Optional<CartItem> cartItemOptional = cartRepository.findCartItemByUserIdAndItemId(userId, itemId);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            log.info("Item ID: {} already in cart for user ID: {}. Updating quantity.", itemId, userId);
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartRepository.save(cartItem);
        } else {
            log.info("Item ID: {} not in cart for user ID: {}. Creating new cart item.", itemId, userId);
            CartItem newCartItem = CartItem.builder()
                .itemId(itemId)
                .userId(userId)
                .quantity(quantity)
                .build();
            return cartRepository.save(newCartItem);
        }
    }

    public List<CartItemDto> getCartItems(String bearerToken) {
        long userId = networkService.getIdFromToken(bearerToken);
        log.info("Fetching cart items for user ID: {}", userId);

        List<CartItem> cartItems = cartRepository.findAllByUserId(userId).orElse(new ArrayList<>());
        if (cartItems.isEmpty()) {
            log.info("Cart is empty for user ID: {}", userId);
            return new ArrayList<>();
        }

        log.info("Found {} items in cart for user ID: {}. Fetching details.", cartItems.size(), userId);
        return cartItems.stream()
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
        log.info("Attempting to remove item ID: {} with quantity: {} from cart for user ID: {}", itemId, quantity, userId);

        CartItem cartItem = cartRepository.findCartItemByUserIdAndItemId(userId, itemId).orElseThrow(
            () -> {
                log.warn("Removal failed: Cart item with ID: {} not found for user ID: {}", itemId, userId);
                return new NoSuchElementException("Cannot find cart item with id " + itemId + " for user " + userId);
            }
        );

        if (cartItem.getQuantity() > quantity) {
            log.info("Decreasing quantity for item ID: {} by {}.", itemId, quantity);
            cartItem.setQuantity(cartItem.getQuantity() - quantity);
            cartRepository.save(cartItem);
        } else {
            log.info("Removing item ID: {} entirely from cart for user ID: {}.", itemId, userId);
            cartRepository.delete(cartItem);
        }
    }

    @Transactional
    public OrderResponse createOrderFromCart(String bearerToken, CreateOrderFromCartRequest request) {
        long userId = networkService.getIdFromToken(bearerToken);
        log.info("Attempting to create an order from cart for user ID: {}", userId);

        List<CartItem> cartItems = cartRepository.findAllByUserId(userId).orElseThrow(
            () -> {
                log.warn("Order creation failed: Cart not found for user ID: {}", userId);
                return new NoSuchElementException("Cannot find cart for user " + userId);
            }
        );

        if (cartItems.isEmpty()) {
            log.warn("Order creation failed: Cart for user ID: {} is empty.", userId);
            throw new IllegalStateException("Cart must contain at least one item to create an order.");
        }

        log.info("Creating order for user ID: {} with {} items.", userId, cartItems.size());
        Order order = Order.builder()
            .userId(userId)
            .orderInitDate(LocalDateTime.now())
            .orderFinishDate(null)
            .deliveryAddress(request.getDeliveryAddress())
            .orderStatus(OrderStatus.INIT)
            .build();

        orderRepository.save(order);
        log.info("Order with ID: {} created successfully. Processing items.", order.getId());

        List<OrderItem> orderItems = new ArrayList<>();
        cartItems.forEach(cartItem -> {
            log.debug("Processing cart item ID: {} for order ID: {}", cartItem.getItemId(), order.getId());
            MenuItemInfoResponse info = networkService.getInfo(cartItem.getItemId());
            OrderItem orderItem = orderItemRepository.save(
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
            log.debug("Processed and deleted cart item ID: {}. Created order item ID: {}", cartItem.getItemId(), orderItem.getId());
        });

        log.info("Finished processing all cart items for order ID: {}", order.getId());
        return OrderMapper.mapToOrderResponse(order, orderItems);
    }
}