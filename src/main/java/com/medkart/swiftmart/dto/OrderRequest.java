package com.medkart.swiftmart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.medkart.swiftmart.entity.Payment;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest {
    private BigDecimal  totalPrice;
    private List<OrderItemRequest> items;

    // future scope
    private Payment payment;
}
