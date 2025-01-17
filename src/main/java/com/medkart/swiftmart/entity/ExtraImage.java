package com.medkart.swiftmart.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "extra_images")
public class ExtraImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.validation.constraints.Pattern(regexp = "^(http|https)://.*", message = "Image URL must be a valid URL")
    @Column(nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

}