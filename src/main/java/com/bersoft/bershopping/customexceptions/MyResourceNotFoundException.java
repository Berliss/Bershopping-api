package com.bersoft.bershopping.customexceptions;

public class MyResourceNotFoundException extends RuntimeException {
    public MyResourceNotFoundException(String message) {
        super(message);
    }
}