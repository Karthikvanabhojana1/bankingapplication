package com.banking.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(GlobalLoggingFilter.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = request.getHeaders().getFirst("X-Request-ID");
        String timestamp = LocalDateTime.now().format(formatter);
        
        // Log incoming request
        logger.info("=== Incoming Request ===");
        logger.info("Request ID: {}", requestId);
        logger.info("Timestamp: {}", timestamp);
        logger.info("Method: {}", request.getMethod());
        logger.info("URI: {}", request.getURI());
        logger.info("Remote Address: {}", request.getRemoteAddress());
        
        long startTime = System.currentTimeMillis();
        
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    // Calculate response time
                    long responseTime = System.currentTimeMillis() - startTime;
                    
                    // Log response
                    logger.info("=== Response Sent ===");
                    logger.info("Request ID: {}", requestId);
                    logger.info("Response Status: {}", exchange.getResponse().getStatusCode());
                    logger.info("Response Time: {}ms", responseTime);
                    logger.info("=== End Request ===\n");
                }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2; // After RequestIdFilter and AuthenticationFilter
    }
}
