package com.banking.gateway.config;

import com.banking.common.dto.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ErrorHandlingConfig {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingConfig.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    @Primary
    @Order(-1)
    public ErrorWebExceptionHandler errorWebExceptionHandler() {
        return new GlobalErrorHandler();
    }

    public static class GlobalErrorHandler implements ErrorWebExceptionHandler {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            HttpStatus status = determineHttpStatus(ex);
            String message = determineErrorMessage(ex);
            String errorCode = determineErrorCode(ex);

            // Log the error
            logger.error("Error occurred in API Gateway: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

            // Create error response
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("timestamp", LocalDateTime.now().format(formatter));
            errorDetails.put("path", exchange.getRequest().getPath().value());
            errorDetails.put("method", exchange.getRequest().getMethod().name());
            errorDetails.put("error-type", ex.getClass().getSimpleName());
            errorDetails.put("error-details", ex.getMessage());

            ApiResponse<Map<String, Object>> errorResponse = ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message(message)
                    .errorCode(errorCode)
                    .data(errorDetails)
                    .build();

            try {
                String errorJson = objectMapper.writeValueAsString(errorResponse);
                DataBuffer buffer = response.bufferFactory().wrap(errorJson.getBytes(StandardCharsets.UTF_8));
                response.setStatusCode(status);
                return response.writeWith(Mono.just(buffer));
            } catch (JsonProcessingException e) {
                logger.error("Error serializing error response", e);
                return Mono.error(e);
            }
        }

        private HttpStatus determineHttpStatus(Throwable ex) {
            if (ex instanceof NotFoundException) {
                return HttpStatus.SERVICE_UNAVAILABLE;
            }
            if (ex instanceof IllegalArgumentException) {
                return HttpStatus.BAD_REQUEST;
            }
            if (ex instanceof SecurityException) {
                return HttpStatus.FORBIDDEN;
            }
            if (ex instanceof RuntimeException) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        private String determineErrorMessage(Throwable ex) {
            if (ex instanceof NotFoundException) {
                return "Service temporarily unavailable. Please try again later.";
            }
            if (ex instanceof IllegalArgumentException) {
                return "Invalid request parameters: " + ex.getMessage();
            }
            if (ex instanceof SecurityException) {
                return "Access denied: " + ex.getMessage();
            }
            if (ex instanceof RuntimeException) {
                return "An unexpected error occurred. Please try again later.";
            }
            return "An error occurred while processing your request.";
        }

        private String determineErrorCode(Throwable ex) {
            if (ex instanceof NotFoundException) {
                return "SERVICE_UNAVAILABLE";
            }
            if (ex instanceof IllegalArgumentException) {
                return "INVALID_REQUEST";
            }
            if (ex instanceof SecurityException) {
                return "ACCESS_DENIED";
            }
            if (ex instanceof RuntimeException) {
                return "INTERNAL_ERROR";
            }
            return "UNKNOWN_ERROR";
        }
    }
}
