package com.medkart.swiftmart.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    @jakarta.validation.constraints.NotBlank
    private String name;

    @Column(nullable = false, length = 500)
    @jakarta.validation.constraints.Size(max = 500)
    @jakarta.validation.constraints.NotBlank
    private String description;

    @jakarta.validation.constraints.Pattern(regexp = "^(http|https)://.*", message = "Image URL must be a valid URL")
    private String imageUrl;

    @Column(nullable = false)
    @jakarta.validation.constraints.DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Column(nullable = false)
    @jakarta.validation.constraints.DecimalMin(value = "0.0", inclusive = false, message = "MRP must be greater than 0")
    private BigDecimal mrp;

    @Column(nullable = false)
    @jakarta.validation.constraints.Min(value = 0, message = "Quantity must not be negative")
    private Long qty;

    @jakarta.validation.constraints.Min(value = 1, message = "Product size must be at least 1")
    @jakarta.validation.constraints.NotNull
    private Long productSize;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)

    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();
}