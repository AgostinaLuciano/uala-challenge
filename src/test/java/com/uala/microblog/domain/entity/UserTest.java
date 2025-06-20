package com.uala.microblog.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserTest {
    
    @Test
    @DisplayName("Should create valid user successfully")
    void testValidUserCreation() {
        
        String username = "alice123";
        String email = "alice@example.com";
        
        
        User user = new User(username, email);
        
        
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getCreatedAt());
        assertDoesNotThrow(() -> user.validate());
    }
    
    @Test
    @DisplayName("Should validate user successfully")
    void testUserValidation_Success() {
        
        User user = new User("validuser", "valid@example.com");
        
        
        assertDoesNotThrow(() -> user.validate());
    }
    
    
    @Test
    @DisplayName("Should reject null username")
    void testUsernameValidation_Null() {
        
        User user = new User(null, "valid@example.com");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> user.validate());
        assertEquals("Username cannot be null or empty", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should reject empty username")
    void testUsernameValidation_Empty() {
        
        User user = new User("", "valid@example.com");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> user.validate());
        assertEquals("Username cannot be null or empty", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should reject whitespace-only username")
    void testUsernameValidation_Whitespace() {
        
        User user = new User("   ", "valid@example.com");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> user.validate());
        assertEquals("Username cannot be null or empty", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should reject username too short")
    void testUsernameValidation_TooShort() {
        
        User user = new User("ab", "valid@example.com");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> user.validate());
        assertEquals("Username must be between 3 and 50 characters", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should reject username too long")
    void testUsernameValidation_TooLong() {
        
        String longUsername = "a".repeat(51);
        User user = new User(longUsername, "valid@example.com");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> user.validate());
        assertEquals("Username must be between 3 and 50 characters", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should accept username with exactly 3 characters")
    void testUsernameValidation_MinLength() {
        
        User user = new User("abc", "valid@example.com");
        
        
        assertDoesNotThrow(() -> user.validate());
    }
    
    @Test
    @DisplayName("Should accept username with exactly 50 characters")
    void testUsernameValidation_MaxLength() {
        
        String username50 = "a".repeat(50);
        User user = new User(username50, "valid@example.com");
        
        
        assertDoesNotThrow(() -> user.validate());
        assertEquals(50, user.getUsername().length());
    }
    
    @Test
    @DisplayName("Should reject username with invalid characters")
    void testUsernameValidation_InvalidCharacters() {
        
        String[] invalidUsernames = {"user-name", "user.name", "user name", "user@test", "user#123"};
        
        for (String invalidUsername : invalidUsernames) {
            User user = new User(invalidUsername, "valid@example.com");
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> user.validate());
            assertEquals("Username can only contain letters, numbers, and underscores", exception.getMessage());
        }
    }
    
    @Test
    @DisplayName("Should accept valid username formats")
    void testUsernameValidation_ValidFormats() {
        
        String[] validUsernames = {"alice123", "user_name", "USER123", "test_123", "a1b2c3"};
        
        for (String validUsername : validUsernames) {
            User user = new User(validUsername, "valid@example.com");
            assertDoesNotThrow(() -> user.validate());
        }
    }
    
    
    @Test
    @DisplayName("Should reject null email")
    void testEmailValidation_Null() {
        
        User user = new User("validuser", null);
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> user.validate());
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should reject empty email")
    void testEmailValidation_Empty() {
        
        User user = new User("validuser", "");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> user.validate());
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should reject invalid email formats")
    void testEmailValidation_InvalidFormats() {
        
        String[] invalidEmails = {"invalid-email", "user@", "@example.com", "user.example.com", "user@.com"};
        
        for (String invalidEmail : invalidEmails) {
            User user = new User("validuser", invalidEmail);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> user.validate());
            assertEquals("Invalid email format", exception.getMessage());
        }
    }
    
    @Test
    @DisplayName("Should accept valid email formats")
    void testEmailValidation_ValidFormats() {
        
        String[] validEmails = {"user@example.com", "test.user@domain.org", "user+tag@example.co.uk"};
        
        for (String validEmail : validEmails) {
            User user = new User("validuser", validEmail);
            assertDoesNotThrow(() -> user.validate());
        }
    }
    
    @Test
    @DisplayName("Should handle user equality correctly")
    void testUserEquality() {
        
        User user1 = new User("507f1f77bcf86cd799439011", "alice", "alice@example.com", java.time.LocalDateTime.now());
        User user2 = new User("507f1f77bcf86cd799439011", "alice", "alice@example.com", java.time.LocalDateTime.now());
        User user3 = new User("507f1f77bcf86cd799439012", "bob", "bob@example.com", java.time.LocalDateTime.now());
        
        
        assertEquals(user1, user2); 
        assertNotEquals(user1, user3); 
        assertEquals(user1.hashCode(), user2.hashCode()); 
    }
} 