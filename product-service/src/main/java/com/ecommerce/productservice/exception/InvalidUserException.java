package com.ecommerce.productservice.exception;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException(Long userId) {
        super("User with id " + userId + " does not exist (validated against User Service)");
    }
}
