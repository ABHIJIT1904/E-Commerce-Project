package com.ecommerce.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    // RestTemplate bean used to make synchronous HTTP calls to other
    // services (e.g. validating a user via User Service). Later, once
    // we introduce Eureka, this gets a @LoadBalanced annotation so it
    // can resolve service names instead of hardcoded URLs.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
