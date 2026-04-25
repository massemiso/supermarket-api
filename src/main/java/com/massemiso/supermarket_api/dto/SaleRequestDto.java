package com.massemiso.supermarket_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SaleRequestDto (
    @NotNull @Min(1) Long branchId,
    @NotNull @NotEmpty List<DetailSaleRequestDto> detailSaleRequestDtoList
) { }
