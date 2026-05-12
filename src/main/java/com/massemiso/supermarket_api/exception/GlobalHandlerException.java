package com.massemiso.supermarket_api.exception;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.massemiso.supermarket_api.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    String errorMessage = "{ " +
        e.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> "[" + fieldError.getField() + "] "
                + fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "))
        +  " }";
    log.error("Parameter Not Valid: {}", errorMessage);
    ApiResponse<Void> apiResponse = ApiResponse.error(
        errorMessage,
        HttpStatus.BAD_REQUEST.value()
    );
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(apiResponse);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Void> handleNoResourceFoundException() {
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

  @ExceptionHandler(UserNotFoundException.class)
  ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException e) {
    return handleNotFoundException(e);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  ResponseEntity<ApiResponse<Void>> handleUsernmeNotFoundException(UsernameNotFoundException e) {
    return handleNotFoundException(e);
  }

  @ExceptionHandler(BadCredentialsException.class)
  ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
    log.error("401 UNAUTHORIZED: {}", e.getMessage());
    ApiResponse<Void> apiResponse = ApiResponse.error(
        e.getMessage(),
        HttpStatus.UNAUTHORIZED.value()
    );
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(apiResponse);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(HttpServletRequest request) {
    String errorMessage = "You do not have the required permissions to perform a "
        + request.getMethod() + " on " + request.getRequestURI();
    log.warn("403 FORBIDDEN: User '{}' doesn't have the required permissions to perform a {} on {}",
        request.getRemoteUser(), request.getMethod(), request.getRequestURI());
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error(errorMessage, 403));
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

    String ipAddress = request.getRemoteAddr();
    String userAgent = request.getHeader("User-Agent");
    log.warn("401 UNAUTHORIZED: User [IP {}, Agent {}] tried to make a {} on {}"
        , ipAddress, userAgent, request.getMethod(), request.getRequestURI());

    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    response.getWriter().write(mapper.writeValueAsString(apiResponse));
  }

}
