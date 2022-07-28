package com.bersoft.bershopping.customexceptions;

public class MyBadOrderException extends RuntimeException {
    public MyBadOrderException(String message) {
        super(message);
    }
}
