package com.massemiso.supermarket_api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.massemiso.supermarket_api.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@ControllerAdvice
public class GlobalHandlerException implements AuthenticationEntryPoint {

  private enum LogType{
    INFO,
    WARN,
    ERROR,
    DEBUG
  }

  // Authentication exceptions
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

  // Authorization exceptions
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(HttpServletRequest request) {
    String apiMsg = "You do not have the required permissions to perform a "
        + request.getMethod() + " on " + request.getRequestURI();
    String logMsg = "403 FORBIDDEN: User '" + request.getRemoteUser() + "'"
        + " doesn't have the required permissions to perform a " + request.getMethod() + " on "
        + request.getRequestURI();
    return handleExceptionsHelper(
        apiMsg,
        HttpStatus.FORBIDDEN,
        logMsg,
        LogType.WARN);
  }

  // General exception handler
  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
    return handleExceptionsHelper(
        e.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Unexpected error occurred: " + e.getMessage(),
        LogType.ERROR);
  }

  // Helper method for handling many similar exceptions
  private ResponseEntity<ApiResponse<Void>> handleExceptionsHelper
  (String apiMsg, HttpStatus status, String logMsg, LogType logType){
    switch (logType){
      case INFO:
        log.info(logMsg);
        break;
      case WARN:
        log.warn(logMsg);
        break;
      case ERROR:
        log.error(logMsg);
        break;
      case DEBUG:
        log.debug(logMsg);
        break;
    }
    ApiResponse<Void> apiResponse = ApiResponse.error(
        apiMsg,
        status.value()
    );
    return ResponseEntity
        .status(status)
        .body(apiResponse);
  }

  @ExceptionHandler(BadCredentialsException.class)
  ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
    return handleExceptionsHelper(
        e.getMessage(),
        HttpStatus.UNAUTHORIZED,
        "401 UNAUTHORIZED: " + e.getMessage(),
        LogType.WARN);
  }

  @ExceptionHandler(UserAlreadyExists.class)
  ResponseEntity<ApiResponse<Void>> handleUserAlreadyExists(UserAlreadyExists e) {
    return handleExceptionsHelper(
        e.getMessage(),
        HttpStatus.CONFLICT,
        "409 CONFLICT: " + e.getMessage(),
        LogType.WARN);
  }

  // Method argument for validations exceptions
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    String apiMsg = "{ " +
        e.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> "[" + fieldError.getField() + "] "
                + fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "))
        +  " }";
    return handleExceptionsHelper(
        apiMsg,
        HttpStatus.BAD_REQUEST,
        "Parameter Not Valid: " + apiMsg,
        LogType.WARN);
  }

  // Not found exception handler helper
  private ResponseEntity<ApiResponse<Void>> handleNotFoundException(Exception e) {
    return handleExceptionsHelper(
        e.getMessage(),
        HttpStatus.NOT_FOUND,
        "RESOURCE: " + e.getMessage(),
        LogType.WARN);
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
  ResponseEntity<ApiResponse<Void>> handleUsernameNotFoundException(UsernameNotFoundException e) {
    return handleNotFoundException(e);
  }

  // No resource found exception handler -> CHECK
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Void> handleNoResourceFoundException() {
    // Just return 404 without logging an ERROR stack trace
    return ResponseEntity.notFound().build();
  }

}
