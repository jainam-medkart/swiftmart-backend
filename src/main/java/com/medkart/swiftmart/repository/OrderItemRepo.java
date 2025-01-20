package com.medkart.swiftmart.repository;

import com.medkart.swiftmart.entity.OrderItem;
import com.medkart.swiftmart.enums.OrderStatus;
import com.medkart.swiftmart.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// Jpa Specification used to create custom query on Repo which used to shorten the code
@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long>, JpaSpecificationExecutor<OrderItem> {

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
            "WHERE oi.user.id = :userId " +
            "AND oi.product.id = :productId " +
            "AND oi.status NOT IN :excludedStatuses")
    int getTotalOrderedQuantityByUserAndProduct(@Param("userId") Long userId,
                                                @Param("productId") Long productId,
                                                @Param("excludedStatuses") List<OrderStatus> excludedStatuses);


    @Query("SELECT oi.product.id, SUM(oi.quantity) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.product.id ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getTopSellingProducts(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}
