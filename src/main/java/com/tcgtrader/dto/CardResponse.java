package com.tcgtrader.dto;

import com.tcgtrader.entity.Card;

import java.math.BigDecimal;
import java.util.UUID;

public record CardResponse(UUID id, UUID itemId, UUID setId, String setName, String name,
                           String rarity, String condition, BigDecimal price, int stock, String imageUrl) {
    public static CardResponse from(Card card) {
        return new CardResponse(
                card.getId(),
                card.getItem().getId(),
                card.getSet().getId(),
                card.getSet().getName(),
                card.getName(),
                card.getRarity(),
                card.getCondition(),
                card.getPrice(),
                card.getStock(),
                card.getImageUrl()
        );
    }
}
