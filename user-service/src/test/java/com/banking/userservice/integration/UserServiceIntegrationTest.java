package com.banking.userservice.integration;

import com.banking.userservice.dto.UserDto;
import com.banking.userservice.entity.UserStatus;
import com.banking.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private UserDto user1;
    private UserDto user2;
    private UserDto user3;

    @BeforeEach
    void setUp() {
        // Setup test data
        user1 = new UserDto();
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPhoneNumber("1234567890");
        user1.setAddress("123 Main Street, New York, NY 10001");

        user2 = new UserDto();
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setPhoneNumber("9876543210");
        user2.setAddress("456 Oak Avenue, Los Angeles, CA 90210");

        user3 = new UserDto();
        user3.setFirstName("Bob");
        user3.setLastName("Johnson");
        user3.setEmail("bob.johnson@example.com");
        user3.setPhoneNumber("5551234567");
        user3.setAddress("789 Pine Road, Chicago, IL 60601");
    }

    @Test
    void testCreateUser_Success() {
        // Act
        UserDto result = userService.createUser(user1);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals("123 Main Street, New York, NY 10001", result.getAddress());
        assertEquals(UserStatus.ACTIVE, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void testCreateMultipleUsers_Success() {
        // Act
        UserDto result1 = userService.createUser(user1);
        UserDto result2 = userService.createUser(user2);
        UserDto result3 = userService.createUser(user3);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertNotEquals(result1.getId(), result2.getId());
        assertNotEquals(result2.getId(), result3.getId());
    }

    @Test
    void testCreateUser_DuplicateEmail_ThrowsException() {
        // Arrange
        userService.createUser(user1);
        UserDto duplicateUser = new UserDto();
        duplicateUser.setFirstName("John");
        duplicateUser.setLastName("Doe");
        duplicateUser.setEmail("john.doe@example.com"); // Same email
        duplicateUser.setPhoneNumber("1112223333");
        duplicateUser.setAddress("Different address");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.createUser(duplicateUser);
        });
    }

    @Test
    void testCreateUser_DuplicatePhoneNumber_ThrowsException() {
        // Arrange
        userService.createUser(user1);
        UserDto duplicateUser = new UserDto();
        duplicateUser.setFirstName("John");
        duplicateUser.setLastName("Doe");
        duplicateUser.setEmail("john.doe2@example.com");
        duplicateUser.setPhoneNumber("1234567890"); // Same phone number
        duplicateUser.setAddress("Different address");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.createUser(duplicateUser);
        });
    }

    @Test
    void testGetUserById_Success() {
        // Arrange
        UserDto created = userService.createUser(user1);

        // Act
        UserDto result = userService.getUserById(created.getId());

        // Assert
        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals(created.getEmail(), result.getEmail());
    }

    @Test
    void testGetUserById_NotFound_ThrowsException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    void testGetUserByEmail_Success() {
        // Arrange
        userService.createUser(user1);

        // Act
        UserDto result = userService.getUserByEmail("john.doe@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testGetUserByEmail_NotFound_ThrowsException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.getUserByEmail("nonexistent@example.com");
        });
    }

    @Test
    void testGetUserByPhoneNumber_Success() {
        // Arrange
        userService.createUser(user1);

        // Act
        UserDto result = userService.getUserByPhoneNumber("1234567890");

        // Assert
        assertNotNull(result);
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testGetUserByPhoneNumber_NotFound_ThrowsException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.getUserByPhoneNumber("9999999999");
        });
    }

    @Test
    void testGetAllUsers_Success() {
        // Arrange
        userService.createUser(user1);
        userService.createUser(user2);
        userService.createUser(user3);

        // Act
        List<UserDto> results = userService.getAllUsers();

        // Assert
        assertNotNull(results);
        assertTrue(results.size() >= 3);
    }

    @Test
    void testGetUsersByStatus_Success() {
        // Arrange
        userService.createUser(user1);
        userService.createUser(user2);

        // Act
        List<UserDto> results = userService.getUsersByStatus(UserStatus.ACTIVE);

        // Assert
        assertNotNull(results);
        assertTrue(results.size() >= 2);
        assertTrue(results.stream().allMatch(u -> u.getStatus().equals(UserStatus.ACTIVE)));
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        UserDto created = userService.createUser(user1);
        created.setFirstName("Jonathan");
        created.setAddress("456 Updated Street, New York, NY 10002");

        // Act
        UserDto result = userService.updateUser(created.getId(), created);

        // Assert
        assertEquals("Jonathan", result.getFirstName());
        assertEquals("456 Updated Street, New York, NY 10002", result.getAddress());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void testUpdateUser_NotFound_ThrowsException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(999L, user1);
        });
    }

    @Test
    void testUpdateUserStatus_Success() {
        // Arrange
        UserDto created = userService.createUser(user1);

        // Act
        UserDto result = userService.updateUserStatus(created.getId(), UserStatus.SUSPENDED);

        // Assert
        assertEquals(UserStatus.SUSPENDED, result.getStatus());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void testUpdateUserStatus_InvalidStatus_ThrowsException() {
        // Arrange
        UserDto created = userService.createUser(user1);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.updateUserStatus(created.getId(), null);
        });
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        UserDto created = userService.createUser(user1);

        // Act
        userService.deleteUser(created.getId());

        // Assert
        assertThrows(RuntimeException.class, () -> {
            userService.getUserById(created.getId());
        });
    }

    @Test
    void testDeleteUser_NotFound_ThrowsException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(999L);
        });
    }

    @Test
    void testSearchUsersByName_Success() {
        // Arrange
        userService.createUser(user1);
        userService.createUser(user2);

        // Act
        List<UserDto> results = userService.searchUsersByName("John");

        // Assert
        assertNotNull(results);
        assertTrue(results.size() >= 1);
        assertTrue(results.stream().anyMatch(u -> 
            u.getFirstName().contains("John") || u.getLastName().contains("John")));
    }

    @Test
    void testSearchUsersByName_NoMatches_ReturnsEmptyList() {
        // Act
        List<UserDto> results = userService.searchUsersByName("NonexistentName");

        // Assert
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    void testCheckUserExists_Success() {
        // Arrange
        UserDto created = userService.createUser(user1);

        // Act
        boolean exists = userService.existsById(created.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    void testCheckUserExists_NonExistentUser_ReturnsFalse() {
        // Act
        boolean exists = userService.existsById(999L);

        // Assert
        assertFalse(exists);
    }

    @Test
    void testCheckUserExistsByEmail_Success() {
        // Arrange
        userService.createUser(user1);

        // Act
        boolean exists = userService.existsByEmail("john.doe@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void testCheckUserExistsByPhoneNumber_Success() {
        // Arrange
        userService.createUser(user1);

        // Act
        boolean exists = userService.existsByPhoneNumber("1234567890");

        // Assert
        assertTrue(exists);
    }

    @Test
    void testCountUsersByStatus_Success() {
        // Arrange
        userService.createUser(user1);
        userService.createUser(user2);

        // Act
        long count = userService.countUsersByStatus(UserStatus.ACTIVE);

        // Assert
        assertTrue(count >= 2);
    }

    @Test
    void testGetUsersByCity_Success() {
        // Arrange
        userService.createUser(user1);
        userService.createUser(user2);

        // Act
        List<UserDto> newYorkUsers = userService.getUsersByCity("New York");
        List<UserDto> losAngelesUsers = userService.getUsersByCity("Los Angeles");

        // Assert
        assertNotNull(newYorkUsers);
        assertNotNull(losAngelesUsers);
        assertTrue(newYorkUsers.size() >= 1);
        assertTrue(losAngelesUsers.size() >= 1);
    }
}
