package com.sky.exception;

public class DishException  extends RuntimeException{
    public DishException() {
    }

    public DishException(String msg) {
        super(msg);
    }
}
