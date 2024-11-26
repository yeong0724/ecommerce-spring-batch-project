package com.ecommerce.exception;

public class IllegalPaymentStateException extends IllegalStateException {
    public IllegalPaymentStateException(String msg) {
        super(msg);
    }
}
