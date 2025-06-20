package com.uala.microblog.infrastructure.controller;

import com.uala.microblog.application.service.FollowService;
import com.uala.microblog.domain.entity.Follow;
import com.uala.microblog.infrastructure.controller.dto.FollowRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
@CrossOrigin(origins = "*")
public class FollowController {
    
    private final FollowService followService;
    
    public FollowController(FollowService followService) {
        this.followService = followService;
    }
    
    @PostMapping
    public ResponseEntity<Follow> followUser(@Valid @RequestBody FollowRequest request) {
        Follow follow = followService.followUser(request.getFollowerId(), request.getFollowedId());
        return ResponseEntity.status(HttpStatus.CREATED).body(follow);
    }
} 