package com.medkart.swiftmart.repository;

import com.medkart.swiftmart.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepo extends JpaRepository<Tag, Long> {
    // Add this method to find tag by name
    Optional<Tag> findByName(String name);
}
