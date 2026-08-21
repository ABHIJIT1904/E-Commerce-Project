package com.ecommerce.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    // Note: this is a simple stock count for display purposes only.
    // The real source of truth for stock reservation lives in the
    // future Inventory Service — Product Service should not be
    // trusted for "is this in stock" decisions during checkout.
    @Column(nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    // The ID of the user (from User Service) who created/owns this
    // listing, e.g. a seller/admin. We store just the ID here, never
    // a copy of the user's data — that would violate service data
    // ownership. If we need user details, we call User Service.
    @Column(nullable = false)
    private Long createdByUserId;

    @Column(nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
