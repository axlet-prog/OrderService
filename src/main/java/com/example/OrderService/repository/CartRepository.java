package com.example.OrderService.repository;

import com.example.OrderService.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {

    Optional<List<CartItem>> findAllByUserId(Long userId);

    Optional<CartItem> findCartItemByUserIdAndItemId(Long userId, Long itemId);

    void deleteAllByUserId(Long userId);
}
