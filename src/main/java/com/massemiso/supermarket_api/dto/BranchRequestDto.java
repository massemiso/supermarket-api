package com.massemiso.supermarket_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Branch request dto")
public record BranchRequestDto (
    @Schema(example = "Umbrella")
    @NotBlank String name,

    @Schema(example = "123 Main St, Racoon City")
    @NotBlank String address,

    @Schema(example = "555-1234")
    @NotBlank String phoneNumber
){

}
