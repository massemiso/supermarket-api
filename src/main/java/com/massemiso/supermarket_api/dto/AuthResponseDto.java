package com.massemiso.supermarket_api.dto;

public record AuthResponseDto (
    String username,
    String token,
    Boolean status
) { }
