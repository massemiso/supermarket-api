package com.massemiso.supermarket_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequestDto(
    @NotBlank @Size(min = 3, message = "Username must be at least 3 characters long")
    String username,
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
