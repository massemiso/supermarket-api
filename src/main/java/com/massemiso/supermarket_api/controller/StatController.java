package com.massemiso.supermarket_api.controller;

import com.massemiso.supermarket_api.dto.ApiResponse;
import com.massemiso.supermarket_api.dto.ProductResponseDto;
import com.massemiso.supermarket_api.service.StatService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  @GetMapping("/best-selling-product")
  public ResponseEntity<ApiResponse<Pair<ProductResponseDto, BigDecimal>>> getBestSellingProduct(){
    ApiResponse<Pair<ProductResponseDto, BigDecimal>> apiResponse = ApiResponse.success(
        statService.getBestSellingProduct(),
        "Get best selling product successfully",
        HttpStatus.OK.value()
    );
    return  ResponseEntity.ok(apiResponse);
  }

}
