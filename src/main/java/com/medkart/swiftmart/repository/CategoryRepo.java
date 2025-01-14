package com.medkart.swiftmart.repository;

import com.medkart.swiftmart.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}
