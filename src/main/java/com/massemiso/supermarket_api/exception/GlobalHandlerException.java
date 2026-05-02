package com.massemiso.supermarket_api.exception;

import com.massemiso.supermarket_api.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalHandlerException {

  private ResponseEntity<ApiResponse<Void>> handleNotFoundException(Exception e) {
    log.warn("RESOURCE: {}", e.getMessage());
    ApiResponse<Void> apiResponse = ApiResponse.error(
        e.getMessage(),
        HttpStatus.NOT_FOUND.value()
    );
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(apiResponse);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
    log.error("Unexpected error occurred: ", e);
    ApiResponse<Void> apiResponse = ApiResponse.error(
        e.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR.value()
    );
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(apiResponse);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException e) {
    // Just return 404 without logging an ERROR stack trace
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(ProductNotFoundException.class)
  ResponseEntity<ApiResponse<Void>> handleProductNotFoundException(ProductNotFoundException e) {
    return handleNotFoundException(e);
  }

  @ExceptionHandler(BranchNotFoundException.class)
  ResponseEntity<ApiResponse<Void>> handleBranchNotFoundException(BranchNotFoundException e) {
    return handleNotFoundException(e);
  }

  @ExceptionHandler(SaleNotFoundException.class)
  ResponseEntity<ApiResponse<Void>> handleSaleNotFoundException(SaleNotFoundException e) {
    return handleNotFoundException(e);
  }
}
