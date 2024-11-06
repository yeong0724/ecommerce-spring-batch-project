package com.ecommerce.exception;

public class StockQuantityArithmeticException extends ArithmeticException {
    public StockQuantityArithmeticException() {
        super("재고를 더 증가시킬 수 없습니다.");
    }
}
