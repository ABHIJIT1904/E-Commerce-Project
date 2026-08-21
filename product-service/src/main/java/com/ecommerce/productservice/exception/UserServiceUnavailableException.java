package com.ecommerce.productservice.exception;

// Thrown when User Service can't be reached at all (network error,
// service down, timeout) — as opposed to InvalidUserException, which
// means User Service responded but said "no such user". This
// distinction matters: one is a client error (400s), the other is a
// dependency failure (503) that a circuit breaker should eventually
// handle gracefully instead of just failing every request.
public class UserServiceUnavailableException extends RuntimeException {
    public UserServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
