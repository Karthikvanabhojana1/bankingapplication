package com.banking.gateway.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ApiGatewayIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testActuatorHealthEndpoint() {
        webTestClient.get()
                .uri("/actuator/health")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void testActuatorInfoEndpoint() {
        webTestClient.get()
                .uri("/actuator/info")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testActuatorMetricsEndpoint() {
        webTestClient.get()
                .uri("/actuator/metrics")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGatewayRoutingToUserService() {
        // Test that the gateway can route to user service endpoints
        // This will fail if the user service is not running, but that's expected
        webTestClient.get()
                .uri("/api/users/test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError(); // Expected when service is not running
    }

    @Test
    void testGatewayRoutingToAccountService() {
        // Test that the gateway can route to account service endpoints
        webTestClient.get()
                .uri("/api/accounts/test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError(); // Expected when service is not running
    }

    @Test
    void testGatewayRoutingToTransactionService() {
        // Test that the gateway can route to transaction service endpoints
        webTestClient.get()
                .uri("/api/transactions/test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError(); // Expected when service is not running
    }

    @Test
    void testGatewayRoutingToPaymentService() {
        // Test that the gateway can route to payment service endpoints
        webTestClient.get()
                .uri("/api/payments/test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError(); // Expected when service is not running
    }

    @Test
    void testGatewayRoutingToNotificationService() {
        // Test that the gateway can route to notification service endpoints
        webTestClient.get()
                .uri("/api/notifications/test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError(); // Expected when service is not running
    }

    @Test
    void testGatewayRoutingToAdminService() {
        // Test that the gateway can route to admin service endpoints
        webTestClient.get()
                .uri("/api/admin/test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError(); // Expected when service is not running
    }

    @Test
    void testPublicEndpointsAreAccessible() {
        // Test that public endpoints (like registration and login) are accessible
        webTestClient.get()
                .uri("/api/users/register")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError(); // Expected when service is not running
    }

    @Test
    void testProtectedEndpointsRequireAuthentication() {
        // Test that protected endpoints return 401 when no authentication is provided
        webTestClient.get()
                .uri("/api/users/profile")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testNotFoundEndpoint() {
        // Test that non-existent endpoints return 404
        webTestClient.get()
                .uri("/non-existent-endpoint")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testMethodNotAllowed() {
        // Test that unsupported HTTP methods return 405
        webTestClient.delete()
                .uri("/api/users/test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    void testCorsHeaders() {
        // Test that CORS headers are present
        webTestClient.options()
                .uri("/api/users/test")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "content-type")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:3000")
                .expectHeader().valueEquals("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS")
                .expectHeader().valueEquals("Access-Control-Allow-Headers", "Content-Type,Authorization");
    }

    @Test
    void testSecurityHeaders() {
        // Test that security headers are present
        webTestClient.get()
                .uri("/actuator/health")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("X-Frame-Options", "DENY")
                .expectHeader().valueEquals("X-XSS-Protection", "1 ; mode=block")
                .expectHeader().valueEquals("Referrer-Policy", "no-referrer");
    }

    @Test
    void testRequestIdGeneration() {
        // Test that request IDs are generated and returned in headers
        webTestClient.get()
                .uri("/actuator/health")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Request-ID");
    }

    @Test
    void testGatewayIdentification() {
        // Test that the gateway identifies itself in headers
        webTestClient.get()
                .uri("/actuator/health")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Gateway-Service", "API Gateway");
    }
}
