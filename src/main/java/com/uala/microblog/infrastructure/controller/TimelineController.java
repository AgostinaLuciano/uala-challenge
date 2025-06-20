package com.uala.microblog.infrastructure.controller;

import com.uala.microblog.application.service.TimelineService;
import com.uala.microblog.domain.entity.Tweet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/timeline")
@CrossOrigin(origins = "*")
public class TimelineController {
    
    private final TimelineService timelineService;
    
    public TimelineController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    @GetMapping
    public ResponseEntity<List<Tweet>> getTimeline(
            @RequestParam("userId") String userId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        
        
        List<Tweet> timeline = timelineService.getUserTimeline(userId, limit);
        return ResponseEntity.ok(timeline);
    }
} 