package com.massemiso.supermarket_api.controller;

import com.massemiso.supermarket_api.dto.ApiResponse;
import com.massemiso.supermarket_api.dto.ProductRequestDto;
import com.massemiso.supermarket_api.dto.ProductResponseDto;
import com.massemiso.supermarket_api.service.ProductService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;

  @Autowired
  public ProductController(ProductService productService){
    this.productService = productService;
  }

  // GET ALL
  @GetMapping
  public ResponseEntity<Page<ProductResponseDto>> getAll(
      Pageable pageable){
    return ResponseEntity.ok(
        productService.getAll(pageable));
  }

  // GET
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

  // POST
  @PostMapping
  public ResponseEntity<ApiResponse<ProductResponseDto>> create(
      @Valid @RequestBody ProductRequestDto requestDto){
    log.debug("POST Product: {}", requestDto);
    ProductResponseDto responseDto = productService.create(requestDto);
    ApiResponse<ProductResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Product created successfully",
        HttpStatus.CREATED.value()
    );

    log.info(
        "New product resource created with ID: {}. Returning CREATED status",
        responseDto.id());
    return ResponseEntity
        .created(URI.create("/api/products/" + responseDto.id()))
        .body(apiResponse);
  }

  // UPDATE
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponseDto>> update(
      @PathVariable Long id,
      @Valid @RequestBody ProductRequestDto requestDto
  ){
    log.debug("PUT Product -> {} with ID: {}", requestDto, id);
    ProductResponseDto responseDto = productService.update(id, requestDto);
    ApiResponse<ProductResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Product updated successfully",
        HttpStatus.OK.value()
    );
    log.info(
        "Updated product resource with ID: {}. Returning OK status",
        responseDto.id());
    return ResponseEntity
        .ok(apiResponse);
  }

  // DELETE
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long id
  ){
    log.debug("DELETE Product with ID: {}", id);
    productService.delete(id);
    log.info(
        "Deleted product resource with ID: {}. Returning NO_CONTENT status",
        id);
    return ResponseEntity
        .noContent()
        .build();
  }

}
