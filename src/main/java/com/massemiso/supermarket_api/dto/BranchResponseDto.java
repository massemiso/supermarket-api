package com.massemiso.supermarket_api.dto;

public record BranchResponseDto (
   Long id,
   String name,
   String address,
   String phoneNumber
){ }
