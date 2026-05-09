package com.massemiso.supermarket_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ApiResponse<T> (
    T content,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timestamp,
    String message,
    int status
){
  public static <T> ApiResponse<T> success(T content, String message, int status){
    return new ApiResponse<>(content, LocalDateTime.now(), message, status);
  }

  public static ApiResponse<Void> error(String message, int status) {
    return new ApiResponse<>(null, LocalDateTime.now(), message, status);
  }
}
