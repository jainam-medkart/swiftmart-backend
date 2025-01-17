package com.medkart.swiftmart.repository;

import com.medkart.swiftmart.entity.ExtraImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtraImageRepo extends JpaRepository<ExtraImage , Long> {
    @Query("SELECT e FROM ExtraImage e WHERE e.product.id = :productId")
    List<ExtraImage> findByProductId(@Param("productId") Long productId);
}
