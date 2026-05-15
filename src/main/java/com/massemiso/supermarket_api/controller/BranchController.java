package com.massemiso.supermarket_api.controller;

import com.massemiso.supermarket_api.dto.ApiResponse;
import com.massemiso.supermarket_api.dto.BranchRequestDto;
import com.massemiso.supermarket_api.dto.BranchResponseDto;
import com.massemiso.supermarket_api.service.BranchService;
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
@RequestMapping("/api/branches")
public class BranchController {

  private final BranchService branchService;

  @Autowired
  public BranchController(BranchService branchService){
    this.branchService = branchService;
  }

  @Operation(
      summary = "Get a page of branches in db, needs authentication"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Get branches successful",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @GetMapping
  public ResponseEntity<Page<BranchResponseDto>> getAll(
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
      Pageable pageable){
    return ResponseEntity.ok(
        branchService.getAll(pageable));
  }

  @Operation(
      summary = "Get branch by id, needs authentication"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Branch retrieved successfully"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "404",
      description = "Not found branch",
      content = @Content
  )
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<BranchResponseDto>> getById(
      @PathVariable Long id){
    BranchResponseDto responseDto = branchService.getById(id);
    ApiResponse<BranchResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Branch retrieved successfully",
        HttpStatus.OK.value()
    );
    return ResponseEntity
        .ok(apiResponse);
  }

  @Operation(
      summary = "Create a new branch, needs authentication and ADMIN role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "201",
      description = "Branch created successfully"
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
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<BranchResponseDto>> create(
      @Valid @RequestBody BranchRequestDto requestDto){
    BranchResponseDto responseDto = branchService.create(requestDto);
    ApiResponse<BranchResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Branch created successfully",
        HttpStatus.CREATED.value()
    );
    return ResponseEntity
        .created(URI.create("/api/branches/" + responseDto.id()))
        .body(apiResponse);
  }

  @Operation(
      summary = "Update an existing branch, needs authentication and ADMIN role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Branch updated successfully"
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
      description = "Not found branch",
      content = @Content
  )
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<BranchResponseDto>> update(
      @PathVariable Long id,
      @Valid @RequestBody BranchRequestDto requestDto
  ){
    BranchResponseDto responseDto = branchService.update(id, requestDto);
    ApiResponse<BranchResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Branch updated successfully",
        HttpStatus.OK.value()
    );
    return ResponseEntity
        .ok(apiResponse);
  }

  @Operation(
      summary = "Soft deletes an existing branch, needs authentication and ADMIN role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "204",
      description = "Delete successful"
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
      description = "Not found branch",
      content = @Content
  )
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long id
  ){
    branchService.delete(id);
    return ResponseEntity
        .noContent()
        .build();
  }

}
