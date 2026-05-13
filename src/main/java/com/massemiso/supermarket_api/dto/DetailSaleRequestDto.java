package com.massemiso.supermarket_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DetailSaleRequestDto(
    @Schema(example = "5")
    @NotNull @Min(1) Integer quantity,

    @Schema(example = "1")
    @NotNull @Min(1) Long productId
) { }
