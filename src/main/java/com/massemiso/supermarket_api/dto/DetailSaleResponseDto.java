package com.massemiso.supermarket_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record DetailSaleResponseDto (
    @Schema(example = "1")
    Long id,

    @Schema(example = "5")
    Integer quantity,

    @Schema(example = "4.50")
    BigDecimal unitPrice,

    @Schema(example = "1")
    Long productId
){ }
