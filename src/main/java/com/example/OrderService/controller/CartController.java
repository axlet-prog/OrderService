package com.example.OrderService.controller;

import com.example.OrderService.controller.dto.cart.CartItemDto;
import com.example.OrderService.controller.dto.cart.CartResponseDto;
import com.example.OrderService.controller.dto.cart.CreateOrderFromCartRequest;
import com.example.OrderService.controller.dto.order.OrderResponse;
import com.example.OrderService.mapper.CartMapper;
import com.example.OrderService.service.CartService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/cart")
@RestController
@Validated
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    @Value("${services.restaurant-service.url}")
    private String restaurantServiceUrl;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        System.out.println(authServiceUrl);
        System.out.println(restaurantServiceUrl);

        return ResponseEntity.ok("pong " + authServiceUrl + " " + restaurantServiceUrl);
    }

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(@RequestHeader("Authorization") String bearerToken) {
        List<CartItemDto> items = cartService.getCartItems(bearerToken);
        System.out.println("Request to GET /cart");
        return ResponseEntity.ok(CartMapper.mapListCartItemsDtoToResponse(items));
    }

    @PostMapping("/add_item")
    public ResponseEntity<Void> addCartItem(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam("itemId") long itemId,
            @RequestParam("quantity") @Min(1) int quantity) {
        cartService.addCardItem(itemId, quantity, bearerToken);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove_item")
    public ResponseEntity<Void> removeMenuItem(@RequestParam("itemId") long itemId, @RequestParam(value = "quantity", defaultValue = "1") int quantity, @RequestHeader("Authorization") String bearerToken) {
        cartService.removeCartItem(itemId, quantity, bearerToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create_order")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody CreateOrderFromCartRequest request
            ) {
        return ResponseEntity.ok(cartService.createOrderFromCart(bearerToken, request));
    }
}
