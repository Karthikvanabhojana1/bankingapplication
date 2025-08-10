package com.banking.userservice.service;

import com.banking.userservice.dto.UserDto;
import com.banking.userservice.entity.User;
import com.banking.userservice.entity.UserStatus;
import com.banking.userservice.entity.UserType;

import java.util.List;
import java.util.Optional;

public interface UserService {
    
    UserDto createUser(UserDto userDto);
    
    UserDto updateUser(Long id, UserDto userDto);
    
    UserDto getUserById(Long id);
    
    UserDto getUserByEmail(String email);
    
    UserDto getUserByPhoneNumber(String phoneNumber);
    
    List<UserDto> getAllUsers();
    
    List<UserDto> getUsersByType(UserType userType);
    
    List<UserDto> getUsersByStatus(UserStatus status);
    
    List<UserDto> searchUsersByName(String name);
    
    List<UserDto> getUsersByCity(String city);
    
    UserDto updateUserStatus(Long id, UserStatus status);
    
    void deleteUser(Long id);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    boolean existsById(Long id);
    
    long countUsersByStatus(UserStatus status);
}
