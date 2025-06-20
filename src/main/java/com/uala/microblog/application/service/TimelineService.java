package com.uala.microblog.application.service;

import com.uala.microblog.domain.entity.Tweet;
import com.uala.microblog.domain.port.TweetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TimelineService {
    
    private static final Logger logger = LoggerFactory.getLogger(TimelineService.class);
    private static final String TIMELINE_KEY_PREFIX = "timeline:";
    private static final int MAX_TIMELINE_SIZE = 800; 
    private static final int DEFAULT_LIMIT = 50;
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final TweetRepository tweetRepository;
    private final FollowService followService;
    
    public TimelineService(RedisTemplate<String, Object> redisTemplate, 
                          TweetRepository tweetRepository,
                          FollowService followService) {
        this.redisTemplate = redisTemplate;
        this.tweetRepository = tweetRepository;
        this.followService = followService;
    }
    
    /**
     * Push Fanout: Distribuye un tweet a los timelines de todos los seguidores
     */
    public void distributeToFollowersTimelines(Tweet tweet) {
        try {
            
            List<String> followerIds = getFollowersIds(tweet.getUserId());
            
            logger.info("Distributing tweet {} to {} followers", tweet.getId(), followerIds.size());
            
            
            followerIds.add(tweet.getUserId());
            
            
            for (String followerId : followerIds) {
                addTweetToUserTimeline(followerId, tweet);
            }
            
            logger.info("Tweet {} successfully distributed to {} timelines", tweet.getId(), followerIds.size());
            
        } catch (Exception e) {
            logger.error("Error distributing tweet {} to followers: {}", tweet.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to distribute tweet to timelines", e);
        }
    }
    
    /**
     * Agrega un tweet al timeline pre-calculado de un usuario específico
     * OPTIMIZADO: Almacena el tweet completo en Redis, no solo el ID
     */
    public void addTweetToUserTimeline(String userId, Tweet tweet) {
        try {
            String timelineKey = TIMELINE_KEY_PREFIX + userId;
            
            
            double score = tweet.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
            
            
            redisTemplate.opsForZSet().add(timelineKey, tweet, score);
            
            
            long timelineSize = redisTemplate.opsForZSet().zCard(timelineKey);
            if (timelineSize > MAX_TIMELINE_SIZE) {
                
                long toRemove = timelineSize - MAX_TIMELINE_SIZE;
                redisTemplate.opsForZSet().removeRange(timelineKey, 0, toRemove - 1);
            }
            
            
            redisTemplate.expire(timelineKey, java.time.Duration.ofDays(7));
            
            logger.debug("Tweet {} added to timeline of user {}", tweet.getId(), userId);
            
        } catch (Exception e) {
            logger.error("Error adding tweet {} to timeline of user {}: {}", 
                tweet.getId(), userId, e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene el timeline pre-calculado de un usuario desde Redis
     * OPTIMIZADO: Lee tweets completos directamente de Redis, sin consulta a MongoDB
     */
    public List<Tweet> getUserTimeline(String userId, int limit) {
        try {
            String timelineKey = TIMELINE_KEY_PREFIX + userId;
            
            
            Set<Object> tweetsFromRedis = redisTemplate.opsForZSet()
                .reverseRange(timelineKey, 0, limit - 1);
            
            if (tweetsFromRedis.isEmpty()) {
                logger.info("No cached timeline found for user {}, building from scratch", userId);
                return buildTimelineFromScratch(userId, limit);
            }
            
            
            List<Tweet> tweets = tweetsFromRedis.stream()
                .map(obj -> (Tweet) obj)
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .collect(Collectors.toList());
            
            logger.info("Retrieved {} tweets from cached timeline for user {} (NO MongoDB query needed)", 
                tweets.size(), userId);
            return tweets;
            
        } catch (Exception e) {
            logger.error("Error retrieving timeline for user {}: {}", userId, e.getMessage(), e);
            
            return buildTimelineFromScratch(userId, limit);
        }
    }
    
    /**
     * Construye el timeline desde cero cuando no existe en cache (fallback)
     * OPTIMIZADO: También almacena tweets completos en el cache
     */
    private List<Tweet> buildTimelineFromScratch(String userId, int limit) {
        try {
            logger.info("Building timeline from scratch for user {}", userId);
            
            
            List<String> followedUserIds = followService.getFollowedUserIds(userId);
            followedUserIds.add(userId); 
            
            
            List<Tweet> tweets = tweetRepository.findRecentTweetsByUserIds(followedUserIds, limit);
            
            
            if (!tweets.isEmpty()) {
                String timelineKey = TIMELINE_KEY_PREFIX + userId;
                ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
                
                
                for (Tweet tweet : tweets) {
                    double score = tweet.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
                    zSetOps.add(timelineKey, tweet, score);
                }
                
                
                redisTemplate.expire(timelineKey, java.time.Duration.ofDays(7));
                
                logger.info("Timeline cache populated for user {} with {} complete tweets", userId, tweets.size());
            }
            
            return tweets;
            
        } catch (Exception e) {
            logger.error("Error building timeline from scratch for user {}: {}", userId, e.getMessage(), e);
            return List.of(); 
        }
    }
    
    /**
     * Obtiene la lista de seguidores de un usuario (necesario para Push Fanout)
     */
    private List<String> getFollowersIds(String userId) {
        try {
            return followService.getFollowersIds(userId);
        } catch (Exception e) {
            logger.error("Error getting followers for user {}: {}", userId, e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * Invalida el timeline cache de un usuario (útil para follow/unfollow)
     */
    public void invalidateUserTimeline(String userId) {
        try {
            String timelineKey = TIMELINE_KEY_PREFIX + userId;
            redisTemplate.delete(timelineKey);
            logger.info("Timeline cache invalidated for user {}", userId);
        } catch (Exception e) {
            logger.error("Error invalidating timeline for user {}: {}", userId, e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene timeline con límite por defecto
     */
    public List<Tweet> getUserTimeline(String userId) {
        return getUserTimeline(userId, DEFAULT_LIMIT);
    }
    
    /**
     * NUEVO: Invalida un tweet específico de todos los timelines que lo contengan
     * Útil si un tweet se actualiza o elimina
     */
    public void invalidateTweetFromTimelines(Tweet tweet) {
        try {
            
            
            logger.warn("Tweet invalidation from timelines is expensive - consider implementing an inverse index");
            
            
            List<String> affectedUsers = getFollowersIds(tweet.getUserId());
            affectedUsers.add(tweet.getUserId());
            
            for (String userId : affectedUsers) {
                invalidateUserTimeline(userId);
            }
            
        } catch (Exception e) {
            logger.error("Error invalidating tweet {} from timelines: {}", tweet.getId(), e.getMessage(), e);
        }
    }
} 