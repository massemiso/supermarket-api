package com.massemiso.supermarket_api.controller;

import com.massemiso.supermarket_api.dto.ApiResponse;
import com.massemiso.supermarket_api.dto.AuthRequestDto;
import com.massemiso.supermarket_api.dto.AuthResponseDto;
import com.massemiso.supermarket_api.service.UserService;
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

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponseDto>> login(
      @Valid @RequestBody AuthRequestDto authRequestDto
  ){
    ApiResponse<AuthResponseDto> apiResponse = ApiResponse.success(
        userService.login(authRequestDto),
        "Login successfull",
        HttpStatus.OK.value()
    );
   return ResponseEntity.ok(apiResponse);
  }

  // for now only login is implemented
  // users will be registered by admin and manager users with POST /api/users
}
