package com.tcgtrader.service.impl;

import com.tcgtrader.dto.CardRequest;
import com.tcgtrader.dto.CardResponse;
import com.tcgtrader.entity.Card;
import com.tcgtrader.exception.ResourceNotFoundException;
import com.tcgtrader.repository.CardRepository;
import com.tcgtrader.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    @Transactional
    public CardResponse create(CardRequest request) {
        Card card = Card.builder()
                .name(request.name())
                .setCode(request.setCode())
                .rarity(request.rarity())
                .condition(request.condition())
                .price(request.price())
                .stock(request.stock())
                .imageUrl(request.imageUrl())
                .build();
        return CardResponse.from(cardRepository.save(card));
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponse findById(UUID id) {
        return cardRepository.findById(id)
                .map(CardResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardResponse> findAll() {
        return cardRepository.findAll().stream().map(CardResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardResponse> search(String name) {
        return cardRepository.findByNameContainingIgnoreCase(name).stream().map(CardResponse::from).toList();
    }

    @Override
    @Transactional
    public CardResponse update(UUID id, CardRequest request) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + id));
        card.setName(request.name());
        card.setSetCode(request.setCode());
        card.setRarity(request.rarity());
        card.setCondition(request.condition());
        card.setPrice(request.price());
        card.setStock(request.stock());
        card.setImageUrl(request.imageUrl());
        return CardResponse.from(cardRepository.save(card));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!cardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Card not found: " + id);
        }
        cardRepository.deleteById(id);
    }
}
