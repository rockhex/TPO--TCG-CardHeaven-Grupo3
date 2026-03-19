package com.tcgtrader.dto;

import com.tcgtrader.entity.Card;

import java.math.BigDecimal;
import java.util.UUID;

public record CardResponse(UUID id, String name, String setCode, String rarity, String condition,
                           BigDecimal price, int stock, String imageUrl) {
    public static CardResponse from(Card card) {
        return new CardResponse(card.getId(), card.getName(), card.getSetCode(),
                card.getRarity(), card.getCondition(), card.getPrice(), card.getStock(), card.getImageUrl());
    }
}
