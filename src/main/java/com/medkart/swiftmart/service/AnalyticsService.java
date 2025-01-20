package com.medkart.swiftmart.service;

import com.medkart.swiftmart.enums.OrderStatus;
import com.medkart.swiftmart.repository.OrderItemRepo;
import com.medkart.swiftmart.repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final OrderRepo orderRepo;

    private OrderItemRepo orderItemRepo;

    // Fetch Revenue Trends
    public List<Object[]> getRevenueTrends(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepo.getRevenueTrends(startDate, endDate);
    }

    // Fetch Order Status Breakdown
    public Map<OrderStatus, Long> getOrderStatusBreakdown(LocalDate startDate, LocalDate endDate) {
        return orderRepo.getOrderStatusBreakdown(startDate, endDate);
    }

    // Fetch Top Selling Products
    public List<Object[]> getTopSellingProducts(LocalDate startDate, LocalDate endDate) {
        return orderItemRepo.getTopSellingProducts(startDate, endDate);
    }
}
