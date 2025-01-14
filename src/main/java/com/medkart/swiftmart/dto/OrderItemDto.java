package com.medkart.swiftmart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.medkart.swiftmart.entity.Order;
import com.medkart.swiftmart.entity.Product;
import com.medkart.swiftmart.entity.User;
import com.medkart.swiftmart.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

    private Long id;
    private Long quantity;
    private BigDecimal price;
    private BigDecimal mrp;
    private String status;
    private UserDto user;
    private ProductDto product;
    private OrderDto order;

    private LocalDateTime createdAt;
}
