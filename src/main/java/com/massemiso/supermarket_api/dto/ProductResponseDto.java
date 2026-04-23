package com.massemiso.supermarket_api.dto;

import java.math.BigDecimal;

public record ProductResponseDto(
    Long id,
    String name,
    String category,
    BigDecimal actualPrice
) {

}
