package com.massemiso.supermarket_api.controller;

import com.massemiso.supermarket_api.dto.ApiResponse;
import com.massemiso.supermarket_api.dto.UserRequestDto;
import com.massemiso.supermarket_api.dto.UserResponseDto;
import com.massemiso.supermarket_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.net.URI;
import org.springdoc.core.annotations.ParameterObject;
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

  @Operation(
      summary = "Get a page of users in db, needs authentication"+
          ", needs ADMIN or MANAGER role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Get users successful",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "403",
      description = "Not authorized",
      content = @Content
  )
  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<Page<UserResponseDto>> getAll(
      @ParameterObject
      @Schema(
          example = """
              {
                "page": 0,
                "size": 20,
                "sort": "name,asc"
              }
              """
      )
      Pageable pageable
  ){
    return ResponseEntity.ok(
        userService.getAll(pageable));
  }

  @Operation(
      summary = "Get a user by id, needs authentication"+
          ", needs ADMIN or MANAGER role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "User retrieved successfully"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "404",
      description = "Not found user",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "403",
      description = "Not authorized",
      content = @Content
  )
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

  @Operation(
      summary = "Create a user, needs authentication" +
          ", needs ADMIN or MANAGER role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "201",
      description = "User created successfully"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "400",
      description = "Parameter not valid",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "403",
      description = "Not authorized",
      content = @Content
  )
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

  @Operation(
      summary = "Updates an existing user, needs authentication" +
          ", needs ADMIN or MANAGER role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "User updated successfully"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "400",
      description = "Parameter not valid",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "403",
      description = "Not authorized",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "404",
      description = "Not found user",
      content = @Content
  )
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

  @Operation(
      summary = "Soft deletes an existing user, needs authentication" +
          ", needs ADMIN role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "204",
      description = "Delete user successful"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "403",
      description = "Not authorized",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "404",
      description = "Not found user",
      content = @Content
  )
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
