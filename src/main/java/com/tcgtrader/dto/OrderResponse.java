package com.tcgtrader.dto;

import com.tcgtrader.entity.Order;
import com.tcgtrader.entity.OrderItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(UUID id, String status, BigDecimal totalAmount, Instant placedAt, List<OrderItemResponse> items) {

    public record OrderItemResponse(UUID cardId, String cardName, int quantity, BigDecimal unitPrice) {
        public static OrderItemResponse from(OrderItem item) {
            return new OrderItemResponse(item.getCard().getId(), item.getCard().getName(),
                    item.getQuantity(), item.getUnitPrice());
        }
    }

    public static OrderResponse from(Order order) {
        List<OrderItemResponse> items = order.getItems().stream().map(OrderItemResponse::from).toList();
        return new OrderResponse(order.getId(), order.getStatus().name(), order.getTotalAmount(), order.getPlacedAt(), items);
    }
}
