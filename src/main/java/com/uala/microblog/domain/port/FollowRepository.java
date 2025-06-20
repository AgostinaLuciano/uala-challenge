package com.uala.microblog.domain.port;

import com.uala.microblog.domain.entity.Follow;
import java.util.List;

public interface FollowRepository {
    
    Follow save(Follow follow);
    
    List<String> findFollowedUserIdsByFollowerId(String followerId);
    
    List<String> findFollowersByUserId(String userId);
    
    boolean existsByFollowerIdAndFollowedId(String followerId, String followedId);
} 