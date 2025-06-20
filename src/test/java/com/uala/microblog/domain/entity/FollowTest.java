package com.uala.microblog.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Follow Entity Tests")
class FollowTest {
    
    private static final String VALID_USER_ID_1 = "507f1f77bcf86cd799439011";
    private static final String VALID_USER_ID_2 = "507f1f77bcf86cd799439012";
    
    @Test
    @DisplayName("Should create valid follow relationship successfully")
    void testValidFollowCreation() {
        
        String followerId = VALID_USER_ID_1;
        String followedId = VALID_USER_ID_2;
        
        
        Follow follow = new Follow(followerId, followedId);
        
        
        assertNotNull(follow);
        assertEquals(followerId, follow.getFollowerId());
        assertEquals(followedId, follow.getFollowedId());
        assertNotNull(follow.getCreatedAt());
        assertDoesNotThrow(() -> follow.validate());
    }
    
    @Test
    @DisplayName("Should validate follow relationship successfully")
    void testFollowValidation_Success() {
        
        Follow follow = new Follow(VALID_USER_ID_1, VALID_USER_ID_2);
        
        
        assertDoesNotThrow(() -> follow.validate());
    }
    
    
    @Test
    @DisplayName("Should reject null follower ID")
    void testFollowerIdValidation_Null() {
        
        Follow follow = new Follow(null, VALID_USER_ID_2);
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> follow.validate());
        assertEquals("Follower ID is required", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should reject empty follower ID")
    void testFollowerIdValidation_Empty() {
        
        Follow follow = new Follow("", VALID_USER_ID_2);
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> follow.validate());
        assertEquals("Follower ID is required", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should reject whitespace-only follower ID")
    void testFollowerIdValidation_Whitespace() {
        
        Follow follow = new Follow("   ", VALID_USER_ID_2);
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> follow.validate());
        assertEquals("Follower ID is required", exception.getMessage());
    }
    
    
    @Test
    @DisplayName("Should reject null followed ID")
    void testFollowedIdValidation_Null() {
        
        Follow follow = new Follow(VALID_USER_ID_1, null);
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> follow.validate());
        assertEquals("Followed ID is required", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should reject empty followed ID")
    void testFollowedIdValidation_Empty() {
        
        Follow follow = new Follow(VALID_USER_ID_1, "");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> follow.validate());
        assertEquals("Followed ID is required", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should reject whitespace-only followed ID")
    void testFollowedIdValidation_Whitespace() {
        
        Follow follow = new Follow(VALID_USER_ID_1, "   ");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> follow.validate());
        assertEquals("Followed ID is required", exception.getMessage());
    }
    
    
    @Test
    @DisplayName("Should reject user following themselves")
    void testSelfFollowValidation() {
        
        Follow follow = new Follow(VALID_USER_ID_1, VALID_USER_ID_1);
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> follow.validate());
        assertEquals("User cannot follow themselves", exception.getMessage());
    }
    
    
    @Test
    @DisplayName("Should correctly identify recent follow")
    void testIsRecentFollow_Recent() {
        
        Follow follow = new Follow(VALID_USER_ID_1, VALID_USER_ID_2);
        
        
        assertTrue(follow.isRecentFollow());
    }
    
    @Test
    @DisplayName("Should correctly identify old follow")
    void testIsRecentFollow_Old() {
        
        LocalDateTime oldDate = LocalDateTime.now().minusHours(25); 
        Follow follow = new Follow("follow-id", VALID_USER_ID_1, VALID_USER_ID_2, oldDate);
        
        
        assertFalse(follow.isRecentFollow());
    }
    
    @Test
    @DisplayName("Should correctly identify follow at 24-hour boundary")
    void testIsRecentFollow_ExactlyAtBoundary() {
        
        LocalDateTime exactBoundary = LocalDateTime.now().minusHours(24); 
        Follow follow = new Follow("follow-id", VALID_USER_ID_1, VALID_USER_ID_2, exactBoundary);
        
        
        assertFalse(follow.isRecentFollow());
    }
    
    @Test
    @DisplayName("Should handle follow equality correctly")
    void testFollowEquality() {
        
        Follow follow1 = new Follow(VALID_USER_ID_1, VALID_USER_ID_2);
        Follow follow2 = new Follow(VALID_USER_ID_1, VALID_USER_ID_2);
        Follow follow3 = new Follow(VALID_USER_ID_2, VALID_USER_ID_1);
        
        
        assertEquals(follow1, follow2); 
        assertNotEquals(follow1, follow3); 
        assertEquals(follow1.hashCode(), follow2.hashCode()); 
    }
    
    @Test
    @DisplayName("Should handle toString method")
    void testToString() {
        
        Follow follow = new Follow("follow-id", VALID_USER_ID_1, VALID_USER_ID_2, LocalDateTime.now());
        
        
        String result = follow.toString();
        
        
        assertNotNull(result);
        assertTrue(result.contains("Follow{"));
        assertTrue(result.contains(VALID_USER_ID_1));
        assertTrue(result.contains(VALID_USER_ID_2));
    }
} 