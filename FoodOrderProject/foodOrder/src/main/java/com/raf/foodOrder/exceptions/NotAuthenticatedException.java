package com.raf.foodOrder.exceptions;

public class NotAuthenticatedException extends RuntimeException{

    public NotAuthenticatedException(String message) {
        super(message);
    }
}
