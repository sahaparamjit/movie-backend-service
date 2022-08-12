package com.reactivespring.exception;

public class ReviewNotFoundException extends RuntimeException {
    private String message;
    private Throwable ex;

    public ReviewNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.ex = cause;
    }

    public ReviewNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
