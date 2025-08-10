package com.banking.gateway.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class JwtTokenGeneratorTest {
    
    private String testToken;
    private String refreshToken;
    private String operationToken;
    
    @BeforeEach
    void setUp() {
        testToken = JwtTokenGenerator.generateToken("test_user_123", "test@banking.com", "USER");
        refreshToken = JwtTokenGenerator.generateRefreshToken("test_user_123", "test@banking.com");
        operationToken = JwtTokenGenerator.generateOperationToken("test_user_123", "test@banking.com", "USER", "TRANSFER");
    }
    
    @Test
    @DisplayName("Should generate valid JWT token")
    void testGenerateToken() {
        assertNotNull(testToken);
        assertTrue(testToken.length() > 0);
        assertTrue(JwtTokenGenerator.validateToken(testToken));
    }
    
    @Test
    @DisplayName("Should generate valid refresh token")
    void testGenerateRefreshToken() {
        assertNotNull(refreshToken);
        assertTrue(refreshToken.length() > 0);
        assertTrue(JwtTokenGenerator.validateToken(refreshToken));
    }
    
    @Test
    @DisplayName("Should generate valid operation token")
    void testGenerateOperationToken() {
        assertNotNull(operationToken);
        assertTrue(operationToken.length() > 0);
        assertTrue(JwtTokenGenerator.validateToken(operationToken));
    }
    
    @Test
    @DisplayName("Should extract correct user ID from token")
    void testExtractUserId() {
        String userId = JwtTokenGenerator.extractUserId(testToken);
        assertEquals("test_user_123", userId);
    }
    
    @Test
    @DisplayName("Should extract correct role from token")
    void testExtractRole() {
        String role = JwtTokenGenerator.extractRole(testToken);
        assertEquals("USER", role);
    }
    
    @Test
    @DisplayName("Should extract correct email from token")
    void testExtractEmail() {
        String email = JwtTokenGenerator.extractEmail(testToken);
        assertEquals("test@banking.com", email);
    }
    
    @Test
    @DisplayName("Should validate token correctly")
    void testValidateToken() {
        assertTrue(JwtTokenGenerator.validateToken(testToken));
        assertFalse(JwtTokenGenerator.validateToken("invalid.token.here"));
        assertFalse(JwtTokenGenerator.validateToken(""));
        assertFalse(JwtTokenGenerator.validateToken(null));
    }
    
    @Test
    @DisplayName("Should check token expiration correctly")
    void testTokenExpiration() {
        assertFalse(JwtTokenGenerator.isTokenExpired(testToken));
        
        // Generate a token with very short expiration (1 second)
        String shortLivedToken = JwtTokenGenerator.generateToken(
            "test_user", "test@banking.com", "USER", 1000
        );
        
        // Wait for token to expire
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        assertTrue(JwtTokenGenerator.isTokenExpired(shortLivedToken));
    }
    
    @Test
    @DisplayName("Should get correct token expiration time")
    void testGetTokenExpiration() {
        Date expiration = JwtTokenGenerator.getTokenExpiration(testToken);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }
    
    @Test
    @DisplayName("Should generate tokens for all user roles")
    void testGenerateTokensForAllRoles() {
        for (JwtTokenGenerator.UserRole role : JwtTokenGenerator.UserRole.values()) {
            String token = JwtTokenGenerator.generateToken("user_1", "user@banking.com", role.getValue());
            assertNotNull(token);
            assertTrue(JwtTokenGenerator.validateToken(token));
            
            String extractedRole = JwtTokenGenerator.extractRole(token);
            assertEquals(role.getValue(), extractedRole);
        }
    }
    
    @Test
    @DisplayName("Should generate tokens with custom expiration")
    void testGenerateTokenWithCustomExpiration() {
        long customExpiration = 7200000; // 2 hours
        String token = JwtTokenGenerator.generateToken("user_1", "user@banking.com", "USER", customExpiration);
        
        assertNotNull(token);
        assertTrue(JwtTokenGenerator.validateToken(token));
        
        Date expiration = JwtTokenGenerator.getTokenExpiration(token);
        Date issuedAt = new Date(System.currentTimeMillis() - 1000); // 1 second ago
        
        // Check that expiration is approximately customExpiration milliseconds after issuedAt
        long actualExpiration = expiration.getTime() - issuedAt.getTime();
        assertTrue(Math.abs(actualExpiration - customExpiration) < 5000); // Allow 5 second tolerance
    }
    
    @Test
    @DisplayName("Should handle invalid tokens gracefully")
    void testHandleInvalidTokens() {
        assertNull(JwtTokenGenerator.extractUserId("invalid.token.here"));
        assertNull(JwtTokenGenerator.extractRole("invalid.token.here"));
        assertNull(JwtTokenGenerator.extractEmail("invalid.token.here"));
        assertNull(JwtTokenGenerator.getTokenExpiration("invalid.token.here"));
        assertTrue(JwtTokenGenerator.isTokenExpired("invalid.token.here"));
    }
    
    @Test
    @DisplayName("Should generate different tokens for different users")
    void testGenerateDifferentTokensForDifferentUsers() {
        String token1 = JwtTokenGenerator.generateToken("user_1", "user1@banking.com", "USER");
        String token2 = JwtTokenGenerator.generateToken("user_2", "user2@banking.com", "USER");
        
        assertNotEquals(token1, token2);
        assertTrue(JwtTokenGenerator.validateToken(token1));
        assertTrue(JwtTokenGenerator.validateToken(token2));
        
        assertEquals("user_1", JwtTokenGenerator.extractUserId(token1));
        assertEquals("user_2", JwtTokenGenerator.extractUserId(token2));
    }
    
    @Test
    @DisplayName("Should generate different tokens for different operations")
    void testGenerateDifferentTokensForDifferentOperations() {
        String transferToken = JwtTokenGenerator.generateOperationToken("user_1", "user@banking.com", "USER", "TRANSFER");
        String withdrawalToken = JwtTokenGenerator.generateOperationToken("user_1", "user@banking.com", "USER", "WITHDRAWAL");
        
        assertNotEquals(transferToken, withdrawalToken);
        assertTrue(JwtTokenGenerator.validateToken(transferToken));
        assertTrue(JwtTokenGenerator.validateToken(withdrawalToken));
    }
    
    @Test
    @DisplayName("Should maintain token consistency across multiple generations")
    void testTokenConsistency() {
        String token1 = JwtTokenGenerator.generateToken("user_1", "user@banking.com", "USER");
        String token2 = JwtTokenGenerator.generateToken("user_2", "user@banking.com", "USER");
        
        // Tokens should be different due to different user IDs
        assertNotEquals(token1, token2);
        
        // But should have same role and email for same user type
        assertEquals("USER", JwtTokenGenerator.extractRole(token1));
        assertEquals("USER", JwtTokenGenerator.extractRole(token2));
        assertEquals("user@banking.com", JwtTokenGenerator.extractEmail(token1));
        assertEquals("user@banking.com", JwtTokenGenerator.extractEmail(token2));
        
        // User IDs should be different
        assertEquals("user_1", JwtTokenGenerator.extractUserId(token1));
        assertEquals("user_2", JwtTokenGenerator.extractUserId(token2));
    }
}
