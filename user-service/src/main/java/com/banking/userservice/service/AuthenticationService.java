package com.banking.userservice.service;

import com.banking.userservice.dto.LoginRequest;
import com.banking.userservice.dto.LoginResponse;

public interface AuthenticationService {
    LoginResponse login(LoginRequest loginRequest);
    String generateJwtToken(Long userId, String email, String role);
}
