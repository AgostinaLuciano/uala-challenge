package com.uala.microblog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uala.microblog.application.service.FollowService;
import com.uala.microblog.application.service.TimelineService;
import com.uala.microblog.application.service.TweetService;
import com.uala.microblog.application.service.UserService;
import com.uala.microblog.config.TestConfig;
import com.uala.microblog.domain.entity.User;
import com.uala.microblog.domain.entity.Tweet;
import com.uala.microblog.infrastructure.controller.UserController;
import com.uala.microblog.infrastructure.controller.TweetController;
import com.uala.microblog.infrastructure.controller.FollowController;
import com.uala.microblog.infrastructure.controller.TimelineController;
import com.uala.microblog.infrastructure.controller.dto.CreateTweetRequest;
import com.uala.microblog.infrastructure.controller.dto.CreateUserRequest;
import com.uala.microblog.infrastructure.controller.dto.FollowRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(controllers = {UserController.class, TweetController.class, FollowController.class, TimelineController.class})
@Import(TestConfig.class)
@DisplayName("Microblog Integration Tests")
class MicroblogIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private TweetService tweetService;
    
    @MockBean
    private FollowService followService;
    
    @MockBean
    private TimelineService timelineService;
    
    @Nested
    @DisplayName("User Management Tests")
    class UserManagementTests {
        
        @Test
        @DisplayName("Should create valid user successfully")
        void testCreateValidUser() throws Exception {
            // Given
            CreateUserRequest userRequest = new CreateUserRequest("alice123", "alice@example.com");
            User mockUser = new User("test-id", "alice123", "alice@example.com", LocalDateTime.now());
            
            // When
            when(userService.createUser(anyString(), anyString())).thenReturn(mockUser);
            
            // Then
            mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("alice123"))
                    .andExpect(jsonPath("$.email").value("alice@example.com"))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.createdAt").exists());
        }
        
        @Test
        @DisplayName("Should reject invalid username")
        void testCreateUser_InvalidUsername() throws Exception {
            CreateUserRequest userRequest = new CreateUserRequest("ab", "test@example.com");
            
            mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest)))
                    .andExpect(status().isBadRequest());
        }
        
        @Test
        @DisplayName("Should reject invalid email")
        void testCreateUser_InvalidEmail() throws Exception {
            CreateUserRequest userRequest = new CreateUserRequest("validuser", "invalid-email");
            
            mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest)))
                    .andExpect(status().isBadRequest());
        }
    }
    
    @Nested
    @DisplayName("Tweet Management Tests")
    class TweetManagementTests {
        
        @Test
        @DisplayName("Should create valid tweet successfully")
        void testCreateValidTweet() throws Exception {
            // Given
            String userId = "user123";
            CreateTweetRequest tweetRequest = new CreateTweetRequest(userId, "This is a valid tweet!");
            Tweet mockTweet = new Tweet("tweet-id", "This is a valid tweet!", userId, LocalDateTime.now());
            
            // When
            when(tweetService.createTweet(anyString(), anyString())).thenReturn(mockTweet);
            
            // Then
            mockMvc.perform(post("/tweets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tweetRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.content").value("This is a valid tweet!"))
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.createdAt").exists());
        }
        
        @Test
        @DisplayName("Should reject empty tweet content")
        void testCreateTweet_EmptyContent() throws Exception {
            CreateTweetRequest tweetRequest = new CreateTweetRequest("user123", "");
            
            mockMvc.perform(post("/tweets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tweetRequest)))
                    .andExpect(status().isBadRequest());
        }
        
        @Test
        @DisplayName("Should reject tweet exceeding 280 characters")
        void testCreateTweet_ContentTooLong() throws Exception {
            String longContent = "a".repeat(281);
            CreateTweetRequest tweetRequest = new CreateTweetRequest("user123", longContent);
            
            mockMvc.perform(post("/tweets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tweetRequest)))
                    .andExpect(status().isBadRequest());
        }
        
        @Test
        @DisplayName("Should get user tweets successfully")
        void testGetUserTweets() throws Exception {
            // Given
            String userId = "user123";
            Tweet tweet1 = new Tweet("tweet1", "First tweet", userId, LocalDateTime.now());
            Tweet tweet2 = new Tweet("tweet2", "Second tweet", userId, LocalDateTime.now());
            
            // When
            when(userService.getUserTweets(userId)).thenReturn(Arrays.asList(tweet2, tweet1));
            
            // Then
            mockMvc.perform(get("/users/" + userId + "/tweets"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].content").value("Second tweet"))
                    .andExpect(jsonPath("$[1].content").value("First tweet"));
        }
    }
    
    @Nested
    @DisplayName("Follow System Tests")
    class FollowSystemTests {
        
        @Test
        @DisplayName("Should create valid follow relationship")
        void testCreateValidFollow() throws Exception {
            // Given
            String user1Id = "user1";
            String user2Id = "user2";
            FollowRequest followRequest = new FollowRequest(user1Id, user2Id);
            
            // When
            when(followService.followUser(user1Id, user2Id)).thenReturn(new com.uala.microblog.domain.entity.Follow("follow-id", user1Id, user2Id, LocalDateTime.now()));
            when(userService.getFollowing(user1Id)).thenReturn(Arrays.asList(new User("user2", "user2", "user2@example.com", LocalDateTime.now())));
            
            // Then
            mockMvc.perform(post("/follow")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(followRequest)))
                    .andExpect(status().isCreated());
            
            mockMvc.perform(get("/users/" + user1Id + "/following"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value("user2"));
        }
    }
    
    @Nested
    @DisplayName("Timeline Tests")
    class TimelineTests {
        
        @Test
        @DisplayName("Should show empty timeline for user with no follows")
        void testTimeline_NoFollows() throws Exception {
            // Given
            String userId = "user123";
            
            // When
            when(timelineService.getUserTimeline(userId, 50)).thenReturn(Collections.emptyList());
            
            // Then
            mockMvc.perform(get("/timeline")
                    .param("userId", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
        
        @Test
        @DisplayName("Should show tweets from followed users in timeline")
        void testTimeline_ShowFollowedUsersTweets() throws Exception {
            // Given
            String aliceId = "alice";
            String bobId = "bob";
            Tweet bobTweet = new Tweet("tweet-bob", "Bob's tweet", bobId, LocalDateTime.now());
            
            // When
            when(timelineService.getUserTimeline(aliceId, 50)).thenReturn(Arrays.asList(bobTweet));
            
            // Then
            mockMvc.perform(get("/timeline")
                    .param("userId", aliceId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].content").value("Bob's tweet"))
                    .andExpect(jsonPath("$[0].userId").value(bobId));
        }
        
        @Test
        @DisplayName("Should respect timeline limit parameter")
        void testTimeline_LimitParameter() throws Exception {
            // Given
            String userId = "user123";
            Tweet tweet1 = new Tweet("tweet1", "Tweet 1", "followed1", LocalDateTime.now());
            Tweet tweet2 = new Tweet("tweet2", "Tweet 2", "followed2", LocalDateTime.now());
            Tweet tweet3 = new Tweet("tweet3", "Tweet 3", "followed3", LocalDateTime.now());
            
            // When
            when(timelineService.getUserTimeline(userId, 3)).thenReturn(Arrays.asList(tweet3, tweet2, tweet1));
            
            // Then
            mockMvc.perform(get("/timeline")
                    .param("userId", userId)
                    .param("limit", "3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));
        }
    }
} 