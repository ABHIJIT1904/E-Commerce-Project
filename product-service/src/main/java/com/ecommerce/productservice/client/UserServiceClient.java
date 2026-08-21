package com.ecommerce.productservice.client;

import com.ecommerce.productservice.dto.UserResponse;
import com.ecommerce.productservice.exception.InvalidUserException;
import com.ecommerce.productservice.exception.UserServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * This is where Product Service reaches out to User Service over
 * plain HTTP -- this class IS the "microservices moment" of this
 * project. Two independently deployable services, each with their
 * own database, talking over the network instead of a shared
 * in-process method call.
 *
 * Things that can go wrong here that never happened in a monolith:
 *  - User Service could be down (ResourceAccessException / connection refused)
 *  - User Service could be slow (should really have a timeout + circuit breaker -- see README)
 *  - User Service could return 404 (user genuinely doesn't exist)
 *  - Network could be flaky (should retry -- not implemented yet, that's a resilience upgrade)
 */
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${user-service.base-url}")
    private String userServiceBaseUrl;

    public UserResponse getUserById(Long userId) {
        String url = userServiceBaseUrl + "/api/users/" + userId;

        try {
            return restTemplate.getForObject(url, UserResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            // User Service responded, said "no such user" -> client error
            throw new InvalidUserException(userId);
        } catch (ResourceAccessException ex) {
            // Couldn't even reach User Service (down, timeout, DNS, etc.)
            throw new UserServiceUnavailableException(
                    "Could not reach User Service to validate user " + userId, ex);
        }
    }

    public boolean userExists(Long userId) {
        try {
            getUserById(userId);
            return true;
        } catch (InvalidUserException ex) {
            return false;
        }
    }
}
