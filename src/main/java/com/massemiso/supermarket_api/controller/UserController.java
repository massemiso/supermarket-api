package com.massemiso.supermarket_api.controller;

import com.massemiso.supermarket_api.dto.ApiResponse;
import com.massemiso.supermarket_api.dto.UserRequestDto;
import com.massemiso.supermarket_api.dto.UserResponseDto;
import com.massemiso.supermarket_api.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<Page<UserResponseDto>> getAll(
      Pageable pageable
  ){
    return ResponseEntity.ok(
        userService.getAll(pageable));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<ApiResponse<UserResponseDto>> getById(
      @PathVariable Long id
  ){
    UserResponseDto responseDto = userService.getById(id);
    ApiResponse<UserResponseDto> apiResponse = ApiResponse.success(
     responseDto,
     "User retrieved successfully",
     HttpStatus.OK.value()
    );
    return ResponseEntity
        .ok(apiResponse);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<ApiResponse<UserResponseDto>> create(
      @Valid @RequestBody UserRequestDto requestDto){
    UserResponseDto responseDto = userService.create(requestDto);
    ApiResponse<UserResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "User created successfully",
        HttpStatus.CREATED.value()
    );
    return ResponseEntity
        .created(URI.create("/api/users/" + responseDto.id()))
        .body(apiResponse);
  }

  // UPDATE
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<ApiResponse<UserResponseDto>> update(
      @PathVariable Long id,
      @Valid @RequestBody UserRequestDto requestDto
  ){
    UserResponseDto responseDto = userService.update(id, requestDto);
    ApiResponse<UserResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "User updated successfully",
        HttpStatus.OK.value()
    );
    return ResponseEntity
        .ok(apiResponse);
  }

  // DELETE
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long id
  ){
    userService.delete(id);
    return ResponseEntity
        .noContent()
        .build();
  }

}
