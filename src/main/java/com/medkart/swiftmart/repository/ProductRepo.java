package com.medkart.swiftmart.repository;

import com.medkart.swiftmart.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    // For search box
    List<Product> findByNameOrDescriptionContaining(String name, String description);

    // For the category page
    List<Product> findByCategoryId(Long categoryId);
}
