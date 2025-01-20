package com.medkart.swiftmart.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultDTO<T> {

    // Standard response fields
    private int status;
    private String message;
    private final LocalDateTime timestamp = LocalDateTime.now();

    // Optional fields for authentication or metadata
    private String token;
    private String role;
    private String expirationTime;

    // Pagination details
    private int totalPage;
    private long totalElements;

    // Generic response data
    private T data; // Can hold any type of data (e.g., ProductDto, List<ProductDto>)
}
