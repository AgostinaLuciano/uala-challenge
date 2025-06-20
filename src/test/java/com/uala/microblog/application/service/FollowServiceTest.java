package com.uala.microblog.application.service;

import com.uala.microblog.domain.entity.Follow;
import com.uala.microblog.domain.entity.User;
import com.uala.microblog.domain.port.FollowRepository;
import com.uala.microblog.domain.port.UserRepository;
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
@DisplayName("Follow Service Tests")
class FollowServiceTest {
    
    @Mock
    private FollowRepository followRepository;
    
    @Mock
    private UserRepository userRepository;
    
    private FollowService followService;
    
    private static final String FOLLOWER_ID = "507f1f77bcf86cd799439011";
    private static final String FOLLOWED_ID = "507f1f77bcf86cd799439012";
    
    @BeforeEach
    void setUp() {
        followService = new FollowService(followRepository, userRepository);
    }
    
    @Test
    @DisplayName("Should create follow relationship successfully when both users exist")
    void testFollowUser_Success() {
        
        User follower = new User(FOLLOWER_ID, "alice", "alice@example.com", java.time.LocalDateTime.now());
        User followed = new User(FOLLOWED_ID, "bob", "bob@example.com", java.time.LocalDateTime.now());
        Follow expectedFollow = new Follow(FOLLOWER_ID, FOLLOWED_ID);
        
        when(userRepository.findById(FOLLOWER_ID)).thenReturn(Optional.of(follower));
        when(userRepository.findById(FOLLOWED_ID)).thenReturn(Optional.of(followed));
        when(followRepository.existsByFollowerIdAndFollowedId(FOLLOWER_ID, FOLLOWED_ID)).thenReturn(false);
        when(followRepository.save(any(Follow.class))).thenReturn(expectedFollow);
        
        
        Follow result = followService.followUser(FOLLOWER_ID, FOLLOWED_ID);
        
        
        assertNotNull(result);
        assertEquals(FOLLOWER_ID, result.getFollowerId());
        assertEquals(FOLLOWED_ID, result.getFollowedId());
        
        verify(userRepository).findById(FOLLOWER_ID);
        verify(userRepository).findById(FOLLOWED_ID);
        verify(followRepository).existsByFollowerIdAndFollowedId(FOLLOWER_ID, FOLLOWED_ID);
        verify(followRepository).save(any(Follow.class));
    }
    
    @Test
    @DisplayName("Should throw exception when follower does not exist")
    void testFollowUser_FollowerNotFound() {
        
        when(userRepository.findById(FOLLOWER_ID)).thenReturn(Optional.empty());
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> followService.followUser(FOLLOWER_ID, FOLLOWED_ID));
        
        assertEquals("Follower user not found with ID: " + FOLLOWER_ID, exception.getMessage());
        
        verify(userRepository).findById(FOLLOWER_ID);
        verify(userRepository, never()).findById(FOLLOWED_ID);
        verify(followRepository, never()).existsByFollowerIdAndFollowedId(anyString(), anyString());
        verify(followRepository, never()).save(any(Follow.class));
    }
    
    @Test
    @DisplayName("Should throw exception when followed user does not exist")
    void testFollowUser_FollowedNotFound() {
        
        User follower = new User(FOLLOWER_ID, "alice", "alice@example.com", java.time.LocalDateTime.now());
        
        when(userRepository.findById(FOLLOWER_ID)).thenReturn(Optional.of(follower));
        when(userRepository.findById(FOLLOWED_ID)).thenReturn(Optional.empty());
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> followService.followUser(FOLLOWER_ID, FOLLOWED_ID));
        
        assertEquals("Followed user not found with ID: " + FOLLOWED_ID, exception.getMessage());
        
        verify(userRepository).findById(FOLLOWER_ID);
        verify(userRepository).findById(FOLLOWED_ID);
        verify(followRepository, never()).existsByFollowerIdAndFollowedId(anyString(), anyString());
        verify(followRepository, never()).save(any(Follow.class));
    }
    
    @Test
    @DisplayName("Should throw exception when user tries to follow themselves")
    void testFollowUser_SelfFollow() {
        
        User user = new User(FOLLOWER_ID, "alice", "alice@example.com", java.time.LocalDateTime.now());
        
        when(userRepository.findById(FOLLOWER_ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(FOLLOWER_ID)).thenReturn(Optional.of(user));
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> followService.followUser(FOLLOWER_ID, FOLLOWER_ID));
        
        assertEquals("User cannot follow themselves", exception.getMessage());
        
        verify(userRepository, times(2)).findById(FOLLOWER_ID);
        verify(followRepository, never()).existsByFollowerIdAndFollowedId(anyString(), anyString());
        verify(followRepository, never()).save(any(Follow.class));
    }
    
    @Test
    @DisplayName("Should throw exception when follow relationship already exists")
    void testFollowUser_AlreadyExists() {
        
        User follower = new User(FOLLOWER_ID, "alice", "alice@example.com", java.time.LocalDateTime.now());
        User followed = new User(FOLLOWED_ID, "bob", "bob@example.com", java.time.LocalDateTime.now());
        
        when(userRepository.findById(FOLLOWER_ID)).thenReturn(Optional.of(follower));
        when(userRepository.findById(FOLLOWED_ID)).thenReturn(Optional.of(followed));
        when(followRepository.existsByFollowerIdAndFollowedId(FOLLOWER_ID, FOLLOWED_ID)).thenReturn(true);
        
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> followService.followUser(FOLLOWER_ID, FOLLOWED_ID));
        
        assertEquals("User " + FOLLOWER_ID + " is already following user " + FOLLOWED_ID, exception.getMessage());
        
        verify(userRepository).findById(FOLLOWER_ID);
        verify(userRepository).findById(FOLLOWED_ID);
        verify(followRepository).existsByFollowerIdAndFollowedId(FOLLOWER_ID, FOLLOWED_ID);
        verify(followRepository, never()).save(any(Follow.class));
    }
    
    @Test
    @DisplayName("Should throw exception for null follower ID")
    void testFollowUser_NullFollowerId() {
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> followService.followUser(null, FOLLOWED_ID));
        
        assertEquals("Follower ID is required", exception.getMessage());
        
        verify(userRepository, never()).findById(anyString());
        verify(followRepository, never()).existsByFollowerIdAndFollowedId(anyString(), anyString());
        verify(followRepository, never()).save(any(Follow.class));
    }
    
    @Test
    @DisplayName("Should throw exception for empty follower ID")
    void testFollowUser_EmptyFollowerId() {
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> followService.followUser("", FOLLOWED_ID));
        
        assertEquals("Follower ID is required", exception.getMessage());
        
        verify(userRepository, never()).findById(anyString());
        verify(followRepository, never()).existsByFollowerIdAndFollowedId(anyString(), anyString());
        verify(followRepository, never()).save(any(Follow.class));
    }
    
    @Test
    @DisplayName("Should throw exception for null followed ID")
    void testFollowUser_NullFollowedId() {
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> followService.followUser(FOLLOWER_ID, null));
        
        assertEquals("Followed ID is required", exception.getMessage());
        
        verify(userRepository, never()).findById(anyString());
        verify(followRepository, never()).existsByFollowerIdAndFollowedId(anyString(), anyString());
        verify(followRepository, never()).save(any(Follow.class));
    }
    
    @Test
    @DisplayName("Should throw exception for empty followed ID")
    void testFollowUser_EmptyFollowedId() {
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> followService.followUser(FOLLOWER_ID, ""));
        
        assertEquals("Followed ID is required", exception.getMessage());
        
        verify(userRepository, never()).findById(anyString());
        verify(followRepository, never()).existsByFollowerIdAndFollowedId(anyString(), anyString());
        verify(followRepository, never()).save(any(Follow.class));
    }
} 