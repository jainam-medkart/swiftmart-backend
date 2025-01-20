package com.medkart.swiftmart.repository;

import com.medkart.swiftmart.entity.Order;
import com.medkart.swiftmart.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    @Query("SELECT o.createdAt, SUM(o.totalPrice) FROM Order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY o.createdAt ORDER BY o.createdAt ASC")
    List<Object[]> getRevenueTrends(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);


    @Query("SELECT oi.status, COUNT(oi) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.status")
    Map<OrderStatus, Long> getOrderStatusBreakdown(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);


}
