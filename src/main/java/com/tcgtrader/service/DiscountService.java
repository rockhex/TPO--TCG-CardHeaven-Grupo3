package com.tcgtrader.service;

import com.tcgtrader.dto.DiscountRequest;
import com.tcgtrader.dto.DiscountResponse;

import java.util.List;
import java.util.UUID;

public interface DiscountService {
    DiscountResponse create(DiscountRequest request);
    List<DiscountResponse> findByItem(UUID itemId);
    List<DiscountResponse> findActiveByItem(UUID itemId);
}
