package com.banking.userservice.dto;

public class LoginResponse {
    
    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserDto user;
    
    // Default constructor
    public LoginResponse() {}
    
    // Constructor with parameters
    public LoginResponse(String token, String tokenType, Long expiresIn, UserDto user) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
    }
    
    // Getters and setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public Long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public UserDto getUser() {
        return user;
    }
    
    public void setUser(UserDto user) {
        this.user = user;
    }
}
