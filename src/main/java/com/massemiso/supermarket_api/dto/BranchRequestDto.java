package com.massemiso.supermarket_api.dto;

import jakarta.validation.constraints.NotBlank;


public record BranchRequestDto (
    @NotBlank String name,
    @NotBlank String address,
    @NotBlank String phoneNumber
){

}
