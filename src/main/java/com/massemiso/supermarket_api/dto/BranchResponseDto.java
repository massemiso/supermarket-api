package com.massemiso.supermarket_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Branch response dto")
public record BranchResponseDto (
    @Schema(example = "1")
    Long id,

    @Schema(example = "Umbrella")
    String name,

    @Schema(example = "123 Main St, Racoon City")
    String address,

    @Schema(example = "555-1234")
    String phoneNumber
){ }
