package com.massemiso.supermarket_api.exception;

import com.massemiso.supermarket_api.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@ControllerAdvice
public class GlobalHandlerException implements AuthenticationEntryPoint {

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

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
    log.warn("UNAUTHORIZED: {}", e.getMessage());
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error("Access Denied: You do not have the required permissions.", 403));
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

  @ExceptionHandler(UserNotFoundException.class)
  ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException e) {
    return handleNotFoundException(e);
  }

  // Catches Authentication exceptions
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authenticationException) throws IOException, ServletException {
    ApiResponse<Void> apiResponse = ApiResponse.error(
        "Authentication is required to perform a " + request.getMethod()
            + " on " + request.getRequestURI(),
        HttpStatus.UNAUTHORIZED.value()
    );

    log.warn("UNAUTHENTICATED: {}", apiResponse.message());

    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    ObjectMapper mapper = new ObjectMapper();
    response.getWriter().write(mapper.writeValueAsString(apiResponse));
  }

}
