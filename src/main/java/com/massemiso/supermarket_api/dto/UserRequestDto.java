package com.massemiso.supermarket_api.dto;

import com.massemiso.supermarket_api.entity.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record UserRequestDto (
    @Schema(example = "awesker")
    @NotBlank String username,

    @Schema(example = "umbrella_rules")
    @NotBlank String password,

    @Schema(example = "awesker@umbrella.com")
    @NotBlank @Email String email,

    @NotEmpty Set<RoleEnum> roles
){

  @Override
  public String toString() {
    return "UserRequestDto{" +
        "username='" + username + '\'' +
        ", password=[MASKED]" +
        ", email='" + email + '\'' +
        ", roles=" + roles +
        '}';
  }
}
