package com.massemiso.supermarket_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Standard API response wrapper")
public record ApiResponse<T> (
    @Schema(description = "Content of the response")
    T content,

    @Schema(description = "Timestamp of the response")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timestamp,

    @Schema(description = "Human readable message",
        example = "Request completed successfully")
    String message,

    @Schema(description = "HTTP status code", example = "200")
    int status
){
  public static <T> ApiResponse<T> success(T content, String message, int status){
    return new ApiResponse<>(content, LocalDateTime.now(), message, status);
  }

  public static ApiResponse<Void> error(String message, int status) {
    return new ApiResponse<>(null, LocalDateTime.now(), message, status);
  }
}
