package com.massemiso.supermarket_api.controller;

import com.massemiso.supermarket_api.dto.ApiResponse;
import com.massemiso.supermarket_api.dto.SaleRequestDto;
import com.massemiso.supermarket_api.dto.SaleResponseDto;
import com.massemiso.supermarket_api.service.SaleService;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/sales")
public class SaleController {

  private final SaleService saleService;

  @Autowired
  public SaleController(SaleService saleService){
    this.saleService = saleService;
  }

  // GET ALL
  @GetMapping
  public ResponseEntity<Page<SaleResponseDto>> getAll(
      Pageable pageable,
      @RequestParam(required = false) Long branchId,
      @RequestParam(required = false) LocalDate date){
    log.info("REST request to get sales. Filters -> branchId: {}, date: {}, "
        + "page: {}", branchId, date, pageable);
    return ResponseEntity.ok(
        saleService.getAll(pageable, branchId, date));
  }

  // GET
  @GetMapping("/{id}")
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

  // POST
  @PostMapping
  public ResponseEntity<ApiResponse<SaleResponseDto>> create(
      @Valid @RequestBody SaleRequestDto requestDto){
    log.debug("POST Sale: {}", requestDto);
    SaleResponseDto responseDto = saleService.create(requestDto);
    ApiResponse<SaleResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Sale created successfully",
        HttpStatus.CREATED.value()
    );
    log.info(
        "New sale resource created with ID: {}. Returning CREATED status.",
        responseDto.id());
    return ResponseEntity
        .created(URI.create("/api/sales/" + responseDto.id()))
        .body(apiResponse);
  }

  // UPDATE NOT ALLOWED

  // DELETE
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long id
  ){
    log.debug("DELETE Sale by ID: {}", id);
    saleService.delete(id);
    log.info(
        "Deleted sale resource with ID: {}. Returning NO_CONTENT status",
        id);
    return ResponseEntity
        .noContent()
        .build();
  }

}
