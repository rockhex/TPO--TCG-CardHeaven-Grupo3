package com.tcgtrader.service.impl;

import com.tcgtrader.dto.CheckoutRequest;
import com.tcgtrader.dto.OrderResponse;
import com.tcgtrader.entity.*;
import com.tcgtrader.exception.BusinessException;
import com.tcgtrader.exception.ResourceNotFoundException;
import com.tcgtrader.repository.CartRepository;
import com.tcgtrader.repository.OrderRepository;
import com.tcgtrader.repository.UserRepository;
import com.tcgtrader.service.CartService;
import com.tcgtrader.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public OrderResponse checkout(UUID userId, CheckoutRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cannot checkout with an empty cart");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Address address = user.getAddresses().stream()
                .filter(a -> a.getId().equals(request.addressId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + request.addressId()));

        // Deduct stock and build order items
        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            Card card = cartItem.getCard();
            if (card.getStock() < cartItem.getQuantity()) {
                throw new BusinessException("Insufficient stock for: " + card.getName());
            }
            card.setStock(card.getStock() - cartItem.getQuantity());
            return OrderItem.builder()
                    .card(card)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .build();
        }).toList();

        BigDecimal total = orderItems.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .address(address)
                .totalAmount(total)
                .build();

        orderItems.forEach(i -> {
            i.setOrder(order);
            order.getItems().add(i);
        });

        Payment payment = Payment.builder()
                .order(order)
                .provider(request.paymentProvider())
                .externalProcessorId(request.externalProcessorId())
                .amount(total)
                .status(Payment.Status.APPROVED)
                .build();

        order.setPayment(payment);
        order.setStatus(Order.Status.PAID);

        Order saved = orderRepository.save(order);
        cartService.clearCart(userId);

        return OrderResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(UUID id) {
        return orderRepository.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findByUser(UUID userId) {
        return orderRepository.findByUserId(userId).stream().map(OrderResponse::from).toList();
    }
}
