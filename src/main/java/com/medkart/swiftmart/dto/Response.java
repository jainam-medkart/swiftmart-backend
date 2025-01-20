package com.medkart.swiftmart.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.medkart.swiftmart.entity.ExtraImage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    // We use this as template for every response
    private int status;
    private String message;
    private final LocalDateTime timestamp = LocalDateTime.now();

    private String token;
    private String role;
    private String expirationTime;
    private Long productId;

    private int totalPage;
    private long totalElements;

    private AddressDto address;

    private UserDto user;
    private List<UserDto> userList;

    private CategoryDto category;
    private List<CategoryDto> categoryList;

    private ProductDto product;
    private List<ProductDto> productList;

    private OrderDto order;
    private List<OrderDto> orderList;

    private OrderItemDto orderItem;
    private List<OrderItemDto> orderItemList;

    private Set<ExtraImageDto> extraImages;
    private List<ExtraImageDto> data;
}
