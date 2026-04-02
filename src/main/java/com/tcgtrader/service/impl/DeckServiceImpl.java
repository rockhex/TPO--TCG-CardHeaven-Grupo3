package com.tcgtrader.service.impl;

import com.tcgtrader.dto.DeckRequest;
import com.tcgtrader.dto.DeckResponse;
import com.tcgtrader.entity.Deck;
import com.tcgtrader.entity.GameSet;
import com.tcgtrader.entity.Item;
import com.tcgtrader.exception.ResourceNotFoundException;
import com.tcgtrader.repository.DeckRepository;
import com.tcgtrader.repository.GameSetRepository;
import com.tcgtrader.repository.ItemRepository;
import com.tcgtrader.service.DeckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;
    private final GameSetRepository gameSetRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public DeckResponse create(DeckRequest request) {
        GameSet set = gameSetRepository.findById(request.setId())
                .orElseThrow(() -> new ResourceNotFoundException("Set not found: " + request.setId()));
        Item item = itemRepository.save(Item.builder().type("DECK").build());
        Deck deck = Deck.builder()
                .item(item)
                .set(set)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .build();
        return DeckResponse.from(deckRepository.save(deck));
    }

    @Override
    @Transactional(readOnly = true)
    public DeckResponse findById(UUID id) {
        return deckRepository.findById(id)
                .map(DeckResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeckResponse> findAll() {
        return deckRepository.findAll().stream().map(DeckResponse::from).toList();
    }

    @Override
    @Transactional
    public DeckResponse update(UUID id, DeckRequest request) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found: " + id));
        GameSet set = gameSetRepository.findById(request.setId())
                .orElseThrow(() -> new ResourceNotFoundException("Set not found: " + request.setId()));
        deck.setSet(set);
        deck.setName(request.name());
        deck.setDescription(request.description());
        deck.setPrice(request.price());
        deck.setStock(request.stock());
        return DeckResponse.from(deckRepository.save(deck));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!deckRepository.existsById(id)) {
            throw new ResourceNotFoundException("Deck not found: " + id);
        }
        deckRepository.deleteById(id);
    }
}
