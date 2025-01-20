package com.medkart.swiftmart.service.inter;

import com.medkart.swiftmart.dto.OrderRequest;
import com.medkart.swiftmart.dto.Response;
import com.medkart.swiftmart.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderItemServiceInterface {

    public Response placeOrder(OrderRequest orderRequest);
    public Response updateOrderItemStatus(Long orderItemId, String status);
    public Response filterOrderItems(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate, Long itemId, Pageable pageable);

}
