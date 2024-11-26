package com.ecommerce.exception;

public class IllegalOrderStateException extends IllegalStateException {
    public IllegalOrderStateException(String msg) {
        super(msg);
    }
}