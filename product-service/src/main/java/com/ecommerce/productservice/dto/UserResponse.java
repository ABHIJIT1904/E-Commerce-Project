package com.ecommerce.productservice.dto;

import lombok.Getter;
import lombok.Setter;

// This is Product Service's OWN copy of what a "user" looks like from
// its perspective -- just enough fields to know the user exists and
// what their name/email is. It intentionally does NOT mirror User
// Service's entity 1:1. Each service defines its own contract for
// external data it consumes; this keeps services loosely coupled.
@Getter
@Setter
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
}
