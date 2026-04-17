package com.tcgtrader.dto;

import com.tcgtrader.entity.Deck;

import java.math.BigDecimal;
import java.util.UUID;

public record DeckResponse(UUID id, UUID itemId, UUID setId, String setName, String name,
                           String description, BigDecimal price, int stock) {
    public static DeckResponse from(Deck deck) {
        return new DeckResponse(
                deck.getId(),
                deck.getItem().getId(),
                deck.getSet().getId(),
                deck.getSet().getName(),
                deck.getName(),
                deck.getDescription(),
                deck.getPrice(),
                deck.getStock()
        );
    }
}
