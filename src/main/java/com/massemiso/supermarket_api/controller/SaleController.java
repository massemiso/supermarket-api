package com.massemiso.supermarket_api.controller;

import com.massemiso.supermarket_api.dto.ApiResponse;
import com.massemiso.supermarket_api.dto.SaleRequestDto;
import com.massemiso.supermarket_api.dto.SaleResponseDto;
import com.massemiso.supermarket_api.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

  private final SaleService saleService;

  @Autowired
  public SaleController(SaleService saleService){
    this.saleService = saleService;
  }

  @Operation(
      summary = "Get a page of sales in db, needs authentication"+
          ", needs ADMIN, MANAGER or CASHIER role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Get sales successful",
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
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CASHIER')")
  public ResponseEntity<Page<SaleResponseDto>> getAll(
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
      Pageable pageable,
      @RequestParam(required = false) Long branchId,
      @RequestParam(required = false) LocalDate date){
    return ResponseEntity.ok(
        saleService.getAll(pageable, branchId, date));
  }

  @Operation(
      summary = "Get a sale by id, needs authentication"+
          ", needs ADMIN, MANAGER or CASHIER role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Sale retrieved successfully"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "Not authenticated",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "404",
      description = "Not found sale",
      content = @Content
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "403",
      description = "Not authorized",
      content = @Content
  )
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CASHIER')")
  public ResponseEntity<ApiResponse<SaleResponseDto>> getById(
      @PathVariable Long id){
    SaleResponseDto responseDto = saleService.getById(id);
    ApiResponse<SaleResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Sale retrieved successfully",
        HttpStatus.OK.value()
    );
    return ResponseEntity
        .ok(apiResponse);
  }

  @Operation(
      summary = "Create a sale, needs authentication" +
          ", needs ADMIN, MANAGER or CASHIER role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "201",
      description = "Sale created successfully"
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
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
  public ResponseEntity<ApiResponse<SaleResponseDto>> create(
      @Valid @RequestBody SaleRequestDto requestDto){
    SaleResponseDto responseDto = saleService.create(requestDto);
    ApiResponse<SaleResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Sale created successfully",
        HttpStatus.CREATED.value()
    );
    return ResponseEntity
        .created(URI.create("/api/sales/" + responseDto.id()))
        .body(apiResponse);
  }

  // UPDATE NOT ALLOWED

  @Operation(
      summary = "Soft deletes an existing sale, needs authentication" +
          ", needs ADMIN or MANAGER role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "204",
      description = "Delete sale successful"
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
      description = "Not found sale",
      content = @Content
  )
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long id
  ){
    saleService.delete(id);
    return ResponseEntity
        .noContent()
        .build();
  }

}
