package com.medkart.swiftmart.repository;

import com.medkart.swiftmart.entity.ExtraImage;
import com.medkart.swiftmart.entity.Product;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    // For search box
    List<Product> findByNameOrDescriptionContaining(String name, String description);

    // For the category page
    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByIdOrNameContainingIgnoreCase(Long id, String name);

    List<Product> findByWsCodeContainingOrNameContainingIgnoreCase(String wsCode, String name);

    boolean existsByName(String name);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Modifying
    @Query("UPDATE Product p SET p.qty = p.qty - :quantity WHERE p.id = :productId AND p.qty >= :quantity")
    int reduceStock(@Param("productId") Long productId, @Param("quantity") Long quantity);

//    @Query("SELECT e FROM ExtraImage e WHERE e.product.id = :productId")
//    List<ExtraImage> findExtraImagesByProductId(@Param("productId") Long productId);

    @Query("SELECT e.id, e.url FROM ExtraImage e WHERE e.product.id = :productId")
    List<Object[]> findExtraImagesByProductId(@Param("productId") Long productId);

    boolean findByName(@NotBlank String name);
}
