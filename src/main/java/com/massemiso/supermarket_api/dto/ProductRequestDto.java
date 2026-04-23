package com.massemiso.supermarket_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequestDto (
   @NotBlank String name,
   @NotBlank String category,
   @NotNull @DecimalMin("0.0") BigDecimal actualPrice
){ }
