package com.massemiso.supermarket_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record ProductResponseDto(
    @Schema(example = "1")
    Long id,

    @Schema(example = "Virus-T")
    String name,

    @Schema(example = "Antiviral")
    String category,

    @Schema(example = "100.0")
    BigDecimal actualPrice
) {

}
