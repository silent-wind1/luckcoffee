package com.sky.exception;

public class SetmealStatusFailedException extends RuntimeException{

    public SetmealStatusFailedException(){}

    public SetmealStatusFailedException(String msg){
        super(msg);
    }
}
