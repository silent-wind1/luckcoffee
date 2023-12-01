package com.sky.exception;

public class OrderException extends RuntimeException {

    public OrderException() {
    }

    public OrderException(String msg) {
        super(msg);
    }
}
