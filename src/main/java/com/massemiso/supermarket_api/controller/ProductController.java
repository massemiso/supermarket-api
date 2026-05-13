package com.massemiso.supermarket_api.controller;

import com.massemiso.supermarket_api.dto.ApiResponse;
import com.massemiso.supermarket_api.dto.ProductRequestDto;
import com.massemiso.supermarket_api.dto.ProductResponseDto;
import com.massemiso.supermarket_api.service.ProductService;
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
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;

  @Autowired
  public ProductController(ProductService productService){
    this.productService = productService;
  }

  @Operation(
      summary = "Get a page of products in db, needs authentication"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Get products successful"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @GetMapping
  public ResponseEntity<Page<ProductResponseDto>> getAll(
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
        productService.getAll(pageable));
  }

  @Operation(
      summary = "Get product by id, needs authentication"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Product retrieved successfully"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "404",
      description = "Not found product",
      content = @Content
  )
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponseDto>> getById(
      @PathVariable Long id){
    ProductResponseDto responseDto = productService.getById(id);
    ApiResponse<ProductResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Product retrieved successfully",
        HttpStatus.OK.value()
    );
    return ResponseEntity
        .ok(apiResponse);
  }

  @Operation(
      summary = "Create a product, needs authentication" +
          ", needs ADMIN or MANAGER role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "201",
      description = "Product created successfully"
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
  public ResponseEntity<ApiResponse<ProductResponseDto>> create(
      @Valid @RequestBody ProductRequestDto requestDto){
    ProductResponseDto responseDto = productService.create(requestDto);
    ApiResponse<ProductResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Product created successfully",
        HttpStatus.CREATED.value()
    );

    return ResponseEntity
        .created(URI.create("/api/products/" + responseDto.id()))
        .body(apiResponse);
  }

  @Operation(
      summary = "Updates an existing product, needs authentication" +
          ", needs ADMIN or MANAGER role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Product updated successfully"
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
      description = "Not found product",
      content = @Content
  )
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<ApiResponse<ProductResponseDto>> update(
      @PathVariable Long id,
      @Valid @RequestBody ProductRequestDto requestDto
  ){
    ProductResponseDto responseDto = productService.update(id, requestDto);
    ApiResponse<ProductResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Product updated successfully",
        HttpStatus.OK.value()
    );
    return ResponseEntity
        .ok(apiResponse);
  }

  @Operation(
      summary = "Soft deletes an existing product, needs authentication" +
          ", needs ADMIN or MANAGER role"
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
      description = "Not found product",
      content = @Content
  )
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long id
  ){
    productService.delete(id);
    return ResponseEntity
        .noContent()
        .build();
  }

}
