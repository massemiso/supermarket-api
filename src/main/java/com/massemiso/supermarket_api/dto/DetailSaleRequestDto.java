package com.massemiso.supermarket_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DetailSaleRequestDto(
    @NotNull @Min(1) Integer quantity,
    @NotNull @Min(1) Long productId
) { }
