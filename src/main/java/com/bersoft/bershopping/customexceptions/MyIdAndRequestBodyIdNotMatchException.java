package com.bersoft.bershopping.customexceptions;

public class MyIdAndRequestBodyIdNotMatchException extends RuntimeException {

    public MyIdAndRequestBodyIdNotMatchException(String message) {
        super(message);
    }

    public MyIdAndRequestBodyIdNotMatchException() {
        super("uri id & requestbody id does not match");
    }
}
