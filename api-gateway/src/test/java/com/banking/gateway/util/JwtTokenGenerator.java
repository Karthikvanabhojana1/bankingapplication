 package com.banking.gateway.util;

import io.jsonwebtoken.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenGenerator {
    
    private static final String SECRET_KEY = "defaultSecretKeyForBankingApplication2024";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000; // 7 days
    
    // Banking application specific roles
    public enum UserRole {
        USER("USER"),
        ADMIN("ADMIN"),
        BANK_MANAGER("BANK_MANAGER"),
        CUSTOMER_SERVICE("CUSTOMER_SERVICE"),
        FINANCIAL_ADVISOR("FINANCIAL_ADVISOR");
        
        private final String value;
        
        UserRole(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    /**
     * Generate a standard JWT token for a user
     */
    public static String generateToken(String userId, String email, String role) {
        return generateToken(userId, email, role, EXPIRATION_TIME);
    }
    
    /**
     * Generate a JWT token with custom expiration time
     */
    public static String generateToken(String userId, String email, String role, long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        claims.put("userId", userId);
        claims.put("tokenType", "ACCESS");
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
    
    /**
     * Generate a refresh token
     */
    public static String generateRefreshToken(String userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("userId", userId);
        claims.put("tokenType", "REFRESH");
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
    
    /**
     * Generate a token for a specific banking operation
     */
    public static String generateOperationToken(String userId, String email, String role, String operation) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        claims.put("userId", userId);
        claims.put("operation", operation);
        claims.put("tokenType", "OPERATION");
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour for operations
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
    
    /**
     * Validate a JWT token
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Extract user ID from token
     */
    public static String extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Extract role from token
     */
    public static String extractRole(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("role", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Extract email from token
     */
    public static String extractEmail(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("email", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Check if token is expired
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }
    
    /**
     * Get token expiration time
     */
    public static Date getTokenExpiration(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Generate test tokens for different user roles
     */
    public static void generateTestTokens() {
        System.out.println("=== Banking Application Test JWT Tokens ===\n");
        
        // Generate tokens for different user roles
        for (UserRole role : UserRole.values()) {
            String userId = "user_" + role.name().toLowerCase();
            String email = userId + "@banking.com";
            
            String accessToken = generateToken(userId, email, role.getValue());
            String refreshToken = generateRefreshToken(userId, email);
            
            System.out.println("Role: " + role.getValue());
            System.out.println("User ID: " + userId);
            System.out.println("Email: " + email);
            System.out.println("Access Token: " + accessToken);
            System.out.println("Refresh Token: " + refreshToken);
            System.out.println("---");
        }
        
        // Generate operation-specific tokens
        System.out.println("\n=== Operation-Specific Tokens ===");
        String[] operations = {"TRANSFER", "WITHDRAWAL", "DEPOSIT", "LOAN_APPLICATION"};
        for (String operation : operations) {
            String token = generateOperationToken("user_1", "user_1@banking.com", "USER", operation);
            System.out.println(operation + " Token: " + token);
        }
        
        System.out.println("\n=== Usage Examples ===");
        System.out.println("1. Use access tokens in Authorization header:");
        System.out.println("   Authorization: Bearer <access_token>");
        System.out.println("2. Use refresh tokens to get new access tokens");
        System.out.println("3. Operation tokens for specific banking operations");
    }
    
    public static void main(String[] args) {
        // Generate comprehensive test tokens
        generateTestTokens();
        
        // Demonstrate token validation
        System.out.println("\n=== Token Validation Demo ===");
        String testToken = generateToken("1", "test@example.com", "USER");
        System.out.println("Generated Token: " + testToken);
        System.out.println("Is Valid: " + validateToken(testToken));
        System.out.println("User ID: " + extractUserId(testToken));
        System.out.println("Role: " + extractRole(testToken));
        System.out.println("Email: " + extractEmail(testToken));
        System.out.println("Is Expired: " + isTokenExpired(testToken));
        System.out.println("Expires At: " + getTokenExpiration(testToken));
    }
}

