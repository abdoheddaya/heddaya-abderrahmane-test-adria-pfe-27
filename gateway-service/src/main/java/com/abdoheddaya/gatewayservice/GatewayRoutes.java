package com.abdoheddaya.gatewayservice;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutes {
    @Bean
    RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("accounts", r -> r.path("/api/accounts/**").uri("lb://account-service"))
                .route("transactions", r -> r.path("/api/transactions/**").uri("lb://transaction-service"))
                .route("assistant", r -> r.path("/api/chat/**").uri("lb://assistant-service"))
                .build();
    }
}
