package com.massemiso.supermarket_api.dto;

import java.math.BigDecimal;

public record DetailSaleResponseDto (
    Long id,
    Integer quantity,
    BigDecimal unitPrice,
    Long productId
){ }
