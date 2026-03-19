package com.tcgtrader.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CardRequest(
        @NotBlank String name,
        String setCode,
        String rarity,
        String condition,
        @NotNull BigDecimal price,
        @Min(0) int stock,
        String imageUrl
) {}
