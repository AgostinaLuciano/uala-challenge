package com.uala.microblog.application.service;

import com.uala.microblog.domain.entity.Tweet;
import com.uala.microblog.domain.entity.User;
import com.uala.microblog.domain.port.TweetRepository;
import com.uala.microblog.domain.port.UserRepository;
import com.uala.microblog.infrastructure.messaging.FanoutMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tweet Service Tests")
class TweetServiceTest {
    
    @Mock
    private TweetRepository tweetRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private FanoutMessageService fanoutMessageService;
    
    private TweetService tweetService;
    
    private static final String VALID_USER_ID = "507f1f77bcf86cd799439011";
    private static final String VALID_TWEET_CONTENT = "This is a valid tweet content!";
    
    @BeforeEach
    void setUp() {
        tweetService = new TweetService(tweetRepository, userRepository, fanoutMessageService);
    }
    
    @Test
    @DisplayName("Should create tweet successfully when user exists")
    void testCreateTweet_Success() {
        
        User user = new User(VALID_USER_ID, "testuser", "test@example.com", java.time.LocalDateTime.now());
        Tweet expectedTweet = new Tweet(VALID_TWEET_CONTENT, VALID_USER_ID);
        
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
        when(tweetRepository.save(any(Tweet.class))).thenReturn(expectedTweet);
        
        
        Tweet result = tweetService.createTweet(VALID_TWEET_CONTENT, VALID_USER_ID);
        
        
        assertNotNull(result);
        assertEquals(VALID_TWEET_CONTENT, result.getContent());
        assertEquals(VALID_USER_ID, result.getUserId());
        
        verify(userRepository).findById(VALID_USER_ID);
        verify(tweetRepository).save(any(Tweet.class));
        verify(fanoutMessageService).sendFanoutMessage(any(Tweet.class));
    }
    
    @Test
    @DisplayName("Should throw exception when user does not exist")
    void testCreateTweet_UserNotFound() {
        
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.empty());
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> tweetService.createTweet(VALID_TWEET_CONTENT, VALID_USER_ID));
        
        assertEquals("User not found with ID: " + VALID_USER_ID, exception.getMessage());
        
        verify(userRepository).findById(VALID_USER_ID);
        verify(tweetRepository, never()).save(any(Tweet.class));
        verify(fanoutMessageService, never()).sendFanoutMessage(any(Tweet.class));
    }
    
    @Test
    @DisplayName("Should throw exception for null content")
    void testCreateTweet_NullContent() {
        
        User user = new User(VALID_USER_ID, "testuser", "test@example.com", java.time.LocalDateTime.now());
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> tweetService.createTweet(null, VALID_USER_ID));
        
        assertEquals("Tweet content cannot be null or empty", exception.getMessage());
        
        verify(userRepository).findById(VALID_USER_ID);
        verify(tweetRepository, never()).save(any(Tweet.class));
        verify(fanoutMessageService, never()).sendFanoutMessage(any(Tweet.class));
    }
    
    @Test
    @DisplayName("Should throw exception for empty content")
    void testCreateTweet_EmptyContent() {
        
        User user = new User(VALID_USER_ID, "testuser", "test@example.com", java.time.LocalDateTime.now());
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> tweetService.createTweet("", VALID_USER_ID));
        
        assertEquals("Tweet content cannot be null or empty", exception.getMessage());
        
        verify(userRepository).findById(VALID_USER_ID);
        verify(tweetRepository, never()).save(any(Tweet.class));
        verify(fanoutMessageService, never()).sendFanoutMessage(any(Tweet.class));
    }
    
    @Test
    @DisplayName("Should throw exception for content exceeding 280 characters")
    void testCreateTweet_ContentTooLong() {
        
        User user = new User(VALID_USER_ID, "testuser", "test@example.com", java.time.LocalDateTime.now());
        String longContent = "a".repeat(281); 
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> tweetService.createTweet(longContent, VALID_USER_ID));
        
        assertEquals("Tweet content cannot exceed 280 characters", exception.getMessage());
        
        verify(userRepository).findById(VALID_USER_ID);
        verify(tweetRepository, never()).save(any(Tweet.class));
        verify(fanoutMessageService, never()).sendFanoutMessage(any(Tweet.class));
    }
    
    @Test
    @DisplayName("Should accept tweet with exactly 280 characters")
    void testCreateTweet_Exactly280Characters() {
        
        User user = new User(VALID_USER_ID, "testuser", "test@example.com", java.time.LocalDateTime.now());
        String content280 = "a".repeat(280); 
        Tweet expectedTweet = new Tweet(content280, VALID_USER_ID);
        
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
        when(tweetRepository.save(any(Tweet.class))).thenReturn(expectedTweet);
        
        
        Tweet result = tweetService.createTweet(content280, VALID_USER_ID);
        
        
        assertNotNull(result);
        assertEquals(content280, result.getContent());
        assertEquals(280, result.getContent().length());
        
        verify(userRepository).findById(VALID_USER_ID);
        verify(tweetRepository).save(any(Tweet.class));
        verify(fanoutMessageService).sendFanoutMessage(any(Tweet.class));
    }
    
    @Test
    @DisplayName("Should throw exception for null user ID")
    void testCreateTweet_NullUserId() {
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> tweetService.createTweet(VALID_TWEET_CONTENT, null));
        
        assertEquals("User ID is required", exception.getMessage());
        
        verify(userRepository, never()).findById(anyString());
        verify(tweetRepository, never()).save(any(Tweet.class));
        verify(fanoutMessageService, never()).sendFanoutMessage(any(Tweet.class));
    }
    
    @Test
    @DisplayName("Should throw exception for empty user ID")
    void testCreateTweet_EmptyUserId() {
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> tweetService.createTweet(VALID_TWEET_CONTENT, ""));
        
        assertEquals("User ID is required", exception.getMessage());
        
        verify(userRepository, never()).findById(anyString());
        verify(tweetRepository, never()).save(any(Tweet.class));
        verify(fanoutMessageService, never()).sendFanoutMessage(any(Tweet.class));
    }
} 