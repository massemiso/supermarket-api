package com.massemiso.supermarket_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO used to login a user")
public record AuthRequestDto(
    @Schema(description = "Username of the user", example = "manager")
    @NotBlank @Size(min = 3, message = "Username must be at least 3 characters long")
    String username,

    @Schema(description = "Password of the user", example = "manager123")
    @NotBlank @Size(min = 6, message = "Password must be at least 6 characters long")
    String password
) {

  @Override
  public String toString() {
    return "AuthRequestDto{" +
        "username='" + username + '\'' +
        ", password=[MASKED]" +
        '}';
  }
}
