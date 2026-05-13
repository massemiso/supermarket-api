package com.massemiso.supermarket_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SaleRequestDto (
    @Schema(example = "1")
    @NotNull @Min(1) Long branchId,

    @NotEmpty List<DetailSaleRequestDto> detailSaleRequestDtoList
) { }
