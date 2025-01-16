package com.medkart.swiftmart.repository;

import com.medkart.swiftmart.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Jpa Specification used to create custom query on Repo which used to shortnen the code
@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long>, JpaSpecificationExecutor<OrderItem> {
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.user.id = :userId AND oi.product.id = :productId")
    int getTotalOrderedQuantityByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}
