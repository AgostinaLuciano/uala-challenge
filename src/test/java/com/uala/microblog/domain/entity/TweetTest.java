package com.uala.microblog.domain.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TweetTest {
    
    @Test
    void testValidTweetCreation() {
        
        String content = "This is a valid tweet content";
        String userId = "507f1f77bcf86cd799439011"; 
        
        
        Tweet tweet = new Tweet(content, userId);
        
        
        assertNotNull(tweet);
        assertEquals(content, tweet.getContent());
        assertEquals(userId, tweet.getUserId());
        assertNotNull(tweet.getCreatedAt());
    }
    
    @Test
    void testTweetValidation_Success() {
        
        Tweet tweet = new Tweet("Valid tweet content", "507f1f77bcf86cd799439011");
        
        
        assertDoesNotThrow(() -> tweet.validate());
    }
    
    @Test
    void testTweetValidation_NullContent() {
        
        Tweet tweet = new Tweet(null, "507f1f77bcf86cd799439011");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> tweet.validate());
        assertEquals("Tweet content cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void testTweetValidation_EmptyContent() {
        
        Tweet tweet = new Tweet("", "507f1f77bcf86cd799439011");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> tweet.validate());
        assertEquals("Tweet content cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void testTweetValidation_WhitespaceOnlyContent() {
        
        Tweet tweet = new Tweet("   ", "507f1f77bcf86cd799439011");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> tweet.validate());
        assertEquals("Tweet content cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void testTweetValidation_ContentTooLong() {
        
        String longContent = "a".repeat(281); 
        Tweet tweet = new Tweet(longContent, "507f1f77bcf86cd799439011");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> tweet.validate());
        assertEquals("Tweet content cannot exceed 280 characters", exception.getMessage());
    }
    
    @Test
    void testTweetValidation_ContentExactly280Characters() {
        
        String content280 = "a".repeat(280); 
        Tweet tweet = new Tweet(content280, "507f1f77bcf86cd799439011");
        
        
        assertDoesNotThrow(() -> tweet.validate());
        assertEquals(280, tweet.getContent().length());
    }
    
    @Test
    void testTweetValidation_NullUserId() {
        
        Tweet tweet = new Tweet("Valid content", null);
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> tweet.validate());
        assertEquals("User ID is required", exception.getMessage());
    }
    
    @Test
    void testTweetValidation_EmptyUserId() {
        
        Tweet tweet = new Tweet("Valid content", "");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> tweet.validate());
        assertEquals("User ID is required", exception.getMessage());
    }
    
    @Test
    void testTweetValidation_WhitespaceUserId() {
        
        Tweet tweet = new Tweet("Valid content", "   ");
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> tweet.validate());
        assertEquals("User ID is required", exception.getMessage());
    }
    
    @Test
    void testTweetBusinessMethods() {
        
        Tweet tweet = new Tweet("Test content", "507f1f77bcf86cd799439011");
        
        
        assertTrue(tweet.getAgeInMinutes() >= 0);
        assertTrue(tweet.isRecent()); 
    }
    
    @Test
    void testTweetEquality() {
        
        Tweet tweet1 = new Tweet("507f1f77bcf86cd799439011", "Content", "507f1f77bcf86cd799439012", java.time.LocalDateTime.now());
        Tweet tweet2 = new Tweet("507f1f77bcf86cd799439011", "Different Content", "507f1f77bcf86cd799439013", java.time.LocalDateTime.now());
        Tweet tweet3 = new Tweet("507f1f77bcf86cd799439014", "Content", "507f1f77bcf86cd799439012", java.time.LocalDateTime.now());
        
        
        assertEquals(tweet1, tweet2); 
        assertNotEquals(tweet1, tweet3); 
        assertEquals(tweet1.hashCode(), tweet2.hashCode()); 
    }
    
    @Test
    void testTweetMaxContentLengthConstant() {
        assertEquals(280, Tweet.MAX_CONTENT_LENGTH);
    }
} 