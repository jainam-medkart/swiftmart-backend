package com.medkart.swiftmart.controller;

import com.medkart.swiftmart.enums.OrderStatus;
import com.medkart.swiftmart.service.AnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@Slf4j
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    // Endpoint to get Revenue Trends
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROOT_ADMIN')")
    @GetMapping("/revenue-trends")
    public List<Object[]> getRevenueTrends(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        // Convert LocalDate to LocalDateTime (start of the day for startDate, end of the day for endDate)
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // Set to the end of the day

        return analyticsService.getRevenueTrends(startDateTime, endDateTime);
    }


    // Endpoint to get Order Status Breakdown
    @GetMapping("/order-status-breakdown")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROOT_ADMIN')")
    public Map<OrderStatus, Long> getOrderStatusBreakdown(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return analyticsService.getOrderStatusBreakdown(startDate, endDate);
    }

    // Endpoint to get Top Selling Products
    @GetMapping("/top-selling-products")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ROOT_ADMIN')")
    public List<Object[]> getTopSellingProducts(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return analyticsService.getTopSellingProducts(startDate, endDate);
    }
}
