package com.banking.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    ServerHttpResponse response = exchange.getResponse();
                    
                    // Security headers
                    response.getHeaders().add("X-Content-Type-Options", "nosniff");
                    response.getHeaders().add("X-Frame-Options", "DENY");
                    response.getHeaders().add("X-XSS-Protection", "1; mode=block");
                    response.getHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");
                    response.getHeaders().add("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
                    
                    // Content Security Policy
                    response.getHeaders().add("Content-Security-Policy", 
                        "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data: https:; " +
                        "font-src 'self'; " +
                        "connect-src 'self'; " +
                        "media-src 'self'; " +
                        "object-src 'none'; " +
                        "base-uri 'self'; " +
                        "form-action 'self'");
                    
                    // Cache control for sensitive endpoints
                    String path = exchange.getRequest().getPath().value();
                    if (path.contains("/api/") && !path.contains("/public/")) {
                        response.getHeaders().add("Cache-Control", "no-store, no-cache, must-revalidate, private");
                        response.getHeaders().add("Pragma", "no-cache");
                        response.getHeaders().add("Expires", "0");
                    }
                }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE; // This should run last
    }
}
