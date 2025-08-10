package com.banking.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestIdFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RequestIdFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_ID_MDC_KEY = "requestId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = generateRequestId(exchange);
        
        // Add request ID to MDC for logging
        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        
        // Add request ID to response headers
        exchange.getResponse().getHeaders().add(REQUEST_ID_HEADER, requestId);
        
        // Add request ID to request headers if not present
        ServerHttpRequest request = exchange.getRequest();
        if (request.getHeaders().getFirst(REQUEST_ID_HEADER) == null) {
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(REQUEST_ID_HEADER, requestId)
                    .build();
            exchange = exchange.mutate().request(modifiedRequest).build();
        }
        
        logger.debug("Request ID {} generated for path: {}", requestId, request.getPath());
        
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    logger.debug("Request ID {} completed with signal: {}", requestId, signalType);
                    MDC.remove(REQUEST_ID_MDC_KEY);
                });
    }

    private String generateRequestId(ServerWebExchange exchange) {
        String existingRequestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);
        return existingRequestId != null ? existingRequestId : UUID.randomUUID().toString();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // This should run first
    }
}
