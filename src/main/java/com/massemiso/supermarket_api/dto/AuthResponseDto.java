package com.massemiso.supermarket_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for authentication")
public record AuthResponseDto (
    @Schema(description = "Username of the user", example = "manager")
    String username,

    @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String token,

    @Schema(description = "Status of the authentication", example = "true")
    Boolean status
) { }
