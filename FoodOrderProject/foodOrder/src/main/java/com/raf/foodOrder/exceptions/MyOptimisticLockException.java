package com.raf.foodOrder.exceptions;

public class MyOptimisticLockException extends RuntimeException{

    public MyOptimisticLockException(String message) {
        super(message);
    }
}
