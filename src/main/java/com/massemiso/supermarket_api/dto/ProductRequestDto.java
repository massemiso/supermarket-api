package com.massemiso.supermarket_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequestDto (
    @Schema(example = "Virus-T")
    @NotBlank String name,

    @Schema(example = "Antiviral")
    @NotBlank String category,

    @Schema(example = "100.0")
    @NotNull @DecimalMin("0.0") BigDecimal actualPrice
){ }
