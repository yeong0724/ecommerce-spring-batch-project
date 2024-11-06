package com.ecommerce.domain.product;

import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.StockQuantityArithmeticException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {
    @Id
    private String productId;

    private Long sellerId;

    private String category;

    private String productName;

    private LocalDate salesStartDate;

    private LocalDate salesEndDate;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    private String brand;

    private String manufacturer;

    private int salesPrice;

    private int stockQuantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static Product of(
            String productId,
            Long sellerId,
            String category,
            String productName,
            LocalDate salesStartDate,
            LocalDate salesEndDate,
            ProductStatus productStatus,
            String brand,
            String manufacturer,
            int salesPrice,
            int stockQuantity,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Product(
                productId,
                sellerId,
                category,
                productName,
                salesStartDate,
                salesEndDate,
                productStatus,
                brand,
                manufacturer,
                salesPrice,
                stockQuantity,
                createdAt,
                updatedAt
        );
    }

    public void increaseStock(int stockQuantity) {
        int newStockQuantity = this.stockQuantity + stockQuantity;

        if (newStockQuantity < 0) {
            throw new StockQuantityArithmeticException();
        }

        this.stockQuantity = newStockQuantity;
    }

    public void decreaseStock(int stockQuantity) {
        if (this.stockQuantity < stockQuantity) {
            throw new InsufficientStockException(productName);
        }

        this.stockQuantity -= stockQuantity;

        if (this.stockQuantity == 0) {
            productStatus = ProductStatus.OUT_OF_STOCK;
        }
    }
}
