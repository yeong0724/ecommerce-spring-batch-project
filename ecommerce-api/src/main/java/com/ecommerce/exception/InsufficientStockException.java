package com.ecommerce.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productName) {
        super("재고가 부족합니다: " + productName);
    }
}
