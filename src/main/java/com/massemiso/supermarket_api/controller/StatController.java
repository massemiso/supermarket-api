package com.massemiso.supermarket_api.controller;

import com.massemiso.supermarket_api.dto.ApiResponse;
import com.massemiso.supermarket_api.dto.BestSellerResponseDto;
import com.massemiso.supermarket_api.service.StatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatController {

  private final StatService statService;

  @Autowired
  public StatController(StatService statService){
    this.statService = statService;
  }

  @Operation(
      summary = "Get the best selling product, needs authentication"+
          ", needs ADMIN or MANAGER role"
  )
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "Get best selling product successfully"
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
  @GetMapping("/best-selling-product")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<ApiResponse<BestSellerResponseDto>> getBestSellingProduct(){
    ApiResponse<BestSellerResponseDto> apiResponse = ApiResponse.success(
        statService.getBestSellingProduct(),
        "Get best selling product successfully",
        HttpStatus.OK.value()
    );
    return  ResponseEntity.ok(apiResponse);
  }

}
