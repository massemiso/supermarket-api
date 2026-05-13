package com.massemiso.supermarket_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO used to create a user")
public record AuthRegisterRequestDto (
    @Schema(description = "Username of the user", example = "chrisred")
    @NotBlank @Size(min = 3, message = "must be at least 3 characters long")
    String username,

    @Schema(description = "Password of the user", example = "my_super_secret_password")
    @NotBlank @Size(min = 6, message = "must be at least 6 characters long")
    String password,

    @Schema(description = "Email of the user", example = "chisred@bsaa.com")
    @NotBlank @Email
    String email
){
  @Override
  public String toString() {
    return "AuthRegisterRequestDto{" +
        "username='" + username + '\'' +
        ", password=[MASKED]" +
        ", email='" + email + '\'' +
        '}';
  }
}
