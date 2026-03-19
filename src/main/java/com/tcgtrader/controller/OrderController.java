package com.tcgtrader.controller;

import com.tcgtrader.dto.CheckoutRequest;
import com.tcgtrader.dto.OrderResponse;
import com.tcgtrader.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/users/{userId}/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse checkout(@PathVariable UUID userId, @Valid @RequestBody CheckoutRequest request) {
        return orderService.checkout(userId, request);
    }

    @GetMapping("/api/users/{userId}/orders")
    public List<OrderResponse> listByUser(@PathVariable UUID userId) {
        return orderService.findByUser(userId);
    }

    @GetMapping("/api/orders/{id}")
    public OrderResponse findById(@PathVariable UUID id) {
        return orderService.findById(id);
    }
}
