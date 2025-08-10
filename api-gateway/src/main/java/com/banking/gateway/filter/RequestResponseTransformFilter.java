package com.banking.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class RequestResponseTransformFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseTransformFilter.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Transform request
        ServerHttpRequest transformedRequest = transformRequest(exchange.getRequest());
        ServerWebExchange transformedExchange = exchange.mutate().request(transformedRequest).build();

        return chain.filter(transformedExchange)
                .then(Mono.fromRunnable(() -> transformResponse(exchange.getResponse())));
    }

    private ServerHttpRequest transformRequest(ServerHttpRequest request) {
        // Create a mutable copy of the headers
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(request.getHeaders());
        
        // Add request ID if not present
        if (!headers.containsKey("X-Request-ID")) {
            headers.add("X-Request-ID", UUID.randomUUID().toString());
        }
        
        // Add timestamp
        headers.add("X-Request-Timestamp", LocalDateTime.now().format(formatter));
        
        // Add gateway identifier
        headers.add("X-Gateway-ID", "api-gateway");
        headers.add("X-Gateway-Version", "1.0.0");
        
        // Add client IP
        if (request.getRemoteAddress() != null) {
            headers.add("X-Client-IP", request.getRemoteAddress().getAddress().getHostAddress());
        }
        
        // Add user agent if present
        String userAgent = request.getHeaders().getFirst(HttpHeaders.USER_AGENT);
        if (userAgent != null) {
            headers.add("X-User-Agent", userAgent);
        }
        
        logger.debug("Request transformed with headers: {}", headers);
        
        return request.mutate()
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .build();
    }

    private void transformResponse(ServerHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        
        // Add response timestamp
        headers.add("X-Response-Timestamp", LocalDateTime.now().format(formatter));
        
        // Add gateway identifier
        headers.add("X-Gateway-ID", "api-gateway");
        headers.add("X-Gateway-Version", "1.0.0");
        
        // Add processing time header if available
        String requestId = headers.getFirst("X-Request-ID");
        if (requestId != null) {
            headers.add("X-Request-ID", requestId);
        }
        
        // Add security headers
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("X-XSS-Protection", "1; mode=block");
        headers.add("Referrer-Policy", "strict-origin-when-cross-origin");
        
        logger.debug("Response transformed with headers: {}", headers);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 4; // After authentication, logging, and throttling
    }
}
