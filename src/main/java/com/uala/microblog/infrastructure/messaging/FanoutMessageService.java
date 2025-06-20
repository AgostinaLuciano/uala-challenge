package com.uala.microblog.infrastructure.messaging;

import com.uala.microblog.domain.entity.Tweet;
import com.uala.microblog.application.service.TimelineService;
import com.uala.microblog.infrastructure.config.RabbitMQConfig;
import com.uala.microblog.infrastructure.messaging.dto.FanoutMessage;
import com.uala.microblog.domain.port.TweetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FanoutMessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(FanoutMessageService.class);
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private TimelineService timelineService;
    
    @Autowired
    private TweetRepository tweetRepository;
    
    /**
     * Envía un mensaje de fanout a la cola de RabbitMQ
     */
    public void sendFanoutMessage(Tweet tweet) {
        try {
            FanoutMessage message = new FanoutMessage(
                tweet.getId(),
                tweet.getUserId(), 
                tweet.getContent(),
                tweet.getCreatedAt()
            );
            
            logger.info("Sending fanout message for tweet {} to RabbitMQ", tweet.getId());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.FANOUT_EXCHANGE,
                RabbitMQConfig.FANOUT_ROUTING_KEY,
                message
            );
            
            logger.debug("Fanout message sent successfully for tweet {}", tweet.getId());
            
        } catch (Exception e) {
            logger.error("Error sending fanout message for tweet {}: {}", tweet.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to send fanout message", e);
        }
    }
    
    /**
     * Procesa mensajes de fanout desde RabbitMQ
     * PUSH FANOUT: Distribuye el tweet a los timelines de todos los seguidores
     */
    @RabbitListener(queues = "microblog.fanout")
    public void processFanoutMessage(FanoutMessage message) {
        try {
            logger.info("Processing PUSH FANOUT message for tweet {} from user {}", 
                message.getTweetId(), message.getUserId());
            
            
            Tweet tweet = reconstructTweetFromMessage(message);
            
            if (tweet != null) {
                
                timelineService.distributeToFollowersTimelines(tweet);
                
                logger.info("PUSH FANOUT completed for tweet {} - distributed to followers' timelines", 
                    message.getTweetId());
            } else {
                logger.warn("Could not reconstruct tweet {} for fanout processing", message.getTweetId());
            }
            
        } catch (Exception e) {
            logger.error("Error processing PUSH FANOUT message for tweet {}: {}", 
                message.getTweetId(), e.getMessage(), e);
            throw e; 
        }
    }
    
    /**
     * Reconstruye un Tweet desde el mensaje de fanout
     */
    private Tweet reconstructTweetFromMessage(FanoutMessage message) {
        try {
            
            Tweet tweet = new Tweet(
                message.getTweetId(),
                message.getContent(),
                message.getUserId(),
                message.getCreatedAt()
            );
            return tweet;
            
        } catch (Exception e) {
            
            logger.warn("Could not reconstruct tweet from message, querying database for tweet {}", 
                message.getTweetId());
            
            return tweetRepository.findById(message.getTweetId()).orElse(null);
        }
    }
    
    /**
     * Envía notificación de timeline actualizado
     */
    public void sendTimelineUpdateNotification(String userId, String tweetId) {
        try {
            TimelineUpdateMessage message = new TimelineUpdateMessage(userId, tweetId);
            
            logger.debug("Sending timeline update notification for user {} and tweet {}", userId, tweetId);
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.TIMELINE_EXCHANGE,
                RabbitMQConfig.TIMELINE_ROUTING_KEY,
                message
            );
            
        } catch (Exception e) {
            logger.error("Error sending timeline update notification: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Procesa notificaciones de timeline actualizado
     */
    @RabbitListener(queues = "microblog.timeline")
    public void processTimelineUpdate(TimelineUpdateMessage message) {
        try {
            logger.debug("Processing timeline update for user {} and tweet {}", 
                message.getUserId(), message.getTweetId());
            
            
            
        } catch (Exception e) {
            logger.error("Error processing timeline update: {}", e.getMessage(), e);
        }
    }
    
    
    public static class TimelineUpdateMessage {
        private String userId; 
        private String tweetId;
        
        public TimelineUpdateMessage() {}
        
        public TimelineUpdateMessage(String userId, String tweetId) {
            this.userId = userId;
            this.tweetId = tweetId;
        }
        
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getTweetId() { return tweetId; }
        public void setTweetId(String tweetId) { this.tweetId = tweetId; }
        
        @Override
        public String toString() {
            return "TimelineUpdateMessage{userId=" + userId + ", tweetId=" + tweetId + "}";
        }
    }
} 