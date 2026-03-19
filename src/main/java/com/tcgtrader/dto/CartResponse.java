package com.tcgtrader.dto;

import com.tcgtrader.entity.Cart;
import com.tcgtrader.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(UUID id, List<CartItemResponse> items, BigDecimal total) {

    public record CartItemResponse(UUID id, UUID cardId, String cardName, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
        public static CartItemResponse from(CartItem item) {
            BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return new CartItemResponse(item.getId(), item.getCard().getId(),
                    item.getCard().getName(), item.getQuantity(), item.getUnitPrice(), subtotal);
        }
    }

    public static CartResponse from(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream().map(CartItemResponse::from).toList();
        BigDecimal total = items.stream().map(CartItemResponse::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cart.getId(), items, total);
    }
}
