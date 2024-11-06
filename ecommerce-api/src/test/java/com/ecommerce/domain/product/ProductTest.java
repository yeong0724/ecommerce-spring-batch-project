package com.ecommerce.domain.product;

import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.StockQuantityArithmeticException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {
    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.of(
                "PROD001",
                1L,
                "Electronics",
                "Test Product",
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                ProductStatus.AVAILABLE,
                "TestBrand",
                "TestManufacturer",
                1000,
                100,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Stock increase should work correctly")
    void testIncreaseStock() {
        product.increaseStock(50);
        assertEquals(150, product.getStockQuantity());
    }

    @Test
    @DisplayName("Exception should occur when stock increase results in negative value")
    void testIncreaseStockWithNegativeResult() {
        assertThrows(StockQuantityArithmeticException.class, () -> product.increaseStock(Integer.MIN_VALUE));
    }

    @Test
    @DisplayName("Stock decrease should work correctly")
    void testDecreaseStock() {
        product.decreaseStock(50);
        assertEquals(50, product.getStockQuantity());
    }

    @Test
    @DisplayName("Exception should occur when trying to decrease more than available stock")
    void testDecreaseStockWithInsufficientStock() {
        assertThrows(InsufficientStockException.class, () -> product.decreaseStock(150));
    }
}