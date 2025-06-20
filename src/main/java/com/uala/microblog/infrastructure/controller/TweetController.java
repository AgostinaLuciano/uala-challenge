package com.uala.microblog.infrastructure.controller;

import com.uala.microblog.application.service.TweetService;
import com.uala.microblog.domain.entity.Tweet;
import com.uala.microblog.infrastructure.controller.dto.CreateTweetRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tweets")
@CrossOrigin(origins = "*")
public class TweetController {
    
    private final TweetService tweetService;
    
    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }
    
    @PostMapping
    public ResponseEntity<Tweet> createTweet(@Valid @RequestBody CreateTweetRequest request) {
        Tweet tweet = tweetService.createTweet(request.getContent(), request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(tweet);
    }
}
