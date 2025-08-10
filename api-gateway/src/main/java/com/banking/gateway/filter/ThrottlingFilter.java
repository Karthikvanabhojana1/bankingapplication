package com.banking.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ThrottlingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(ThrottlingFilter.class);
    
    @Value("${rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;
    
    @Value("${rate-limit.requests-per-hour:1000}")
    private int requestsPerHour;
    
    @Value("${rate-limit.requests-per-day:10000}")
    private int requestsPerDay;

    private final ConcurrentHashMap<String, ThrottleInfo> throttleMap = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String clientId = getClientIdentifier(request);
        
        if (isThrottled(clientId)) {
            logger.warn("Request throttled for client: {}", clientId);
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("Retry-After", "60");
            return exchange.getResponse().setComplete();
        }
        
        // Increment request count
        incrementRequestCount(clientId);
        
        return chain.filter(exchange);
    }

    private String getClientIdentifier(ServerHttpRequest request) {
        // Try to get user ID from JWT token first
        String userId = request.getHeaders().getFirst("X-User-ID");
        if (userId != null) {
            return "user:" + userId;
        }
        
        // Fall back to IP address
        String ipAddress = request.getRemoteAddress() != null ? 
            request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        return "ip:" + ipAddress;
    }

    private boolean isThrottled(String clientId) {
        ThrottleInfo info = throttleMap.get(clientId);
        if (info == null) {
            return false;
        }
        
        long now = System.currentTimeMillis();
        
        // Check per-minute limit
        if (info.getMinuteCount() >= requestsPerMinute && 
            (now - info.getMinuteStart()) < 60000) {
            return true;
        }
        
        // Check per-hour limit
        if (info.getHourCount() >= requestsPerHour && 
            (now - info.getHourStart()) < 3600000) {
            return true;
        }
        
        // Check per-day limit
        if (info.getDayCount() >= requestsPerDay && 
            (now - info.getDayStart()) < 86400000) {
            return true;
        }
        
        return false;
    }

    private void incrementRequestCount(String clientId) {
        throttleMap.compute(clientId, (key, existing) -> {
            long now = System.currentTimeMillis();
            
            if (existing == null) {
                return new ThrottleInfo(now);
            }
            
            // Reset counters if time period has passed
            if (now - existing.getMinuteStart() >= 60000) {
                existing.resetMinute(now);
            }
            if (now - existing.getHourStart() >= 3600000) {
                existing.resetHour(now);
            }
            if (now - existing.getDayStart() >= 86400000) {
                existing.resetDay(now);
            }
            
            existing.incrementCounts();
            return existing;
        });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 3; // After authentication and logging
    }

    private static class ThrottleInfo {
        private AtomicInteger minuteCount = new AtomicInteger(0);
        private AtomicInteger hourCount = new AtomicInteger(0);
        private AtomicInteger dayCount = new AtomicInteger(0);
        private long minuteStart;
        private long hourStart;
        private long dayStart;

        public ThrottleInfo(long startTime) {
            this.minuteStart = startTime;
            this.hourStart = startTime;
            this.dayStart = startTime;
            incrementCounts();
        }

        public void incrementCounts() {
            minuteCount.incrementAndGet();
            hourCount.incrementAndGet();
            dayCount.incrementAndGet();
        }

        public void resetMinute(long time) {
            minuteCount.set(0);
            minuteStart = time;
        }

        public void resetHour(long time) {
            hourCount.set(0);
            hourStart = time;
        }

        public void resetDay(long time) {
            dayCount.set(0);
            dayStart = time;
        }

        // Getters
        public int getMinuteCount() { return minuteCount.get(); }
        public int getHourCount() { return hourCount.get(); }
        public int getDayCount() { return dayCount.get(); }
        public long getMinuteStart() { return minuteStart; }
        public long getHourStart() { return hourStart; }
        public long getDayStart() { return dayStart; }
    }
}
