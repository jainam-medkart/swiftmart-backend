package com.medkart.swiftmart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class OrderItemRequest {

    private Long productId;
    private Long quantity;

}

