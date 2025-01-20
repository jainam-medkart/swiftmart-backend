package com.medkart.swiftmart.service.impl;

import com.medkart.swiftmart.dto.OrderRequest;
import com.medkart.swiftmart.dto.Response;
import com.medkart.swiftmart.enums.OrderStatus;
import com.medkart.swiftmart.service.inter.OrderItemServiceInterface;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public class OrderItemServiceImpl implements OrderItemServiceInterface {
    @Override
    public Response placeOrder(OrderRequest orderRequest) {
        return null;
    }

    @Override
    public Response updateOrderItemStatus(Long orderItemId, String status) {
        return null;
    }

    @Override
    public Response filterOrderItems(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate, Long itemId, Pageable pageable) {
        return null;
    }
}
