package com.tcgtrader.controller;

import com.tcgtrader.dto.CartItemRequest;
import com.tcgtrader.dto.CartResponse;
import com.tcgtrader.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse getCart(@PathVariable UUID userId) {
        return cartService.getCart(userId);
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse addItem(@PathVariable UUID userId, @Valid @RequestBody CartItemRequest request) {
        return cartService.addItem(userId, request);
    }

    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(@PathVariable UUID userId, @PathVariable UUID itemId,
                                   @Valid @RequestBody CartItemRequest request) {
        return cartService.updateItem(userId, itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(@PathVariable UUID userId, @PathVariable UUID itemId) {
        return cartService.removeItem(userId, itemId);
    }
}
