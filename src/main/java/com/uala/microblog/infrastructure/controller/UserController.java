package com.uala.microblog.infrastructure.controller;

import com.uala.microblog.application.service.UserService;
import com.uala.microblog.domain.entity.Tweet;
import com.uala.microblog.domain.entity.User;
import com.uala.microblog.infrastructure.controller.dto.CreateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request.getUsername(), request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    

//--------------endpoints para desarrollo, no expuestos en entorno productivo---------
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<User>> getFollowers(@PathVariable String userId) {
        List<User> followers = userService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }
    
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<User>> getFollowing(@PathVariable String userId) {
        List<User> following = userService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }
    
    @GetMapping("/{userId}/tweets")
    public ResponseEntity<List<Tweet>> getUserTweets(@PathVariable String userId) {
        List<Tweet> tweets = userService.getUserTweets(userId);
        return ResponseEntity.ok(tweets);
    }
} 