package com.massemiso.supermarket_api.controller;

import com.massemiso.supermarket_api.dto.ApiResponse;
import com.massemiso.supermarket_api.dto.AuthRegisterRequestDto;
import com.massemiso.supermarket_api.dto.AuthRequestDto;
import com.massemiso.supermarket_api.dto.AuthResponseDto;
import com.massemiso.supermarket_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private UserService userService;

  @Operation(
      summary = "Login user, returns token valid for 30 minutes"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Login successful"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "400",
      description = "Parameter not valid" ,
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "Wrong password" ,
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "404",
      description = "User not found" ,
      content = @Content
  )
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponseDto>> login(
      @Valid @RequestBody AuthRequestDto authRequestDto
  ){
    ApiResponse<AuthResponseDto> apiResponse = ApiResponse.success(
        userService.login(authRequestDto),
        "Login successful",
        HttpStatus.OK.value()
    );
   return ResponseEntity.ok(apiResponse);
  }

  // guests can register with POST /api/auth/register but can't choose role
  // they will be registered as guest
  @Operation(
      summary = "Register new user, returns token valid for 30 minutes",
      description = "Register with username, password and email, role will be by default of type 'GUEST'"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "201",
      description = "Register successful"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "400",
      description = "Parameter not valid" ,
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "409",
      description = "User already exists",
      content = @Content
  )
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<AuthResponseDto>> register(
      @Valid @RequestBody AuthRegisterRequestDto requestDto
  ){
    ApiResponse<AuthResponseDto> apiResponse = ApiResponse.success(
        userService.register(requestDto),
        "Registration successful",
        HttpStatus.CREATED.value()
    );
    return ResponseEntity
        .status(HttpStatus.CREATED.value())
        .body(apiResponse);
  }
}
