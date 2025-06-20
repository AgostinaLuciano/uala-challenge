package com.uala.microblog.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class RabbitMQConfig {
    
    
    @Value("${microblog.rabbitmq.queues.fanout:microblog.fanout}")
    private String fanoutQueueName;
    
    @Value("${microblog.rabbitmq.queues.timeline:microblog.timeline}")
    private String timelineQueueName;
    
    @Value("${microblog.rabbitmq.queues.notifications:microblog.notifications}")
    private String notificationsQueueName;
    
    
    public static final String FANOUT_EXCHANGE = "microblog.fanout.exchange";
    public static final String TIMELINE_EXCHANGE = "microblog.timeline.exchange";
    public static final String NOTIFICATIONS_EXCHANGE = "microblog.notifications.exchange";
    
    
    public static final String FANOUT_ROUTING_KEY = "fanout.tweet";
    public static final String TIMELINE_ROUTING_KEY = "timeline.update";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.send";
    
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        return converter;
    }
    
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
    
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        
        admin.setAutoStartup(true);
        admin.setIgnoreDeclarationExceptions(true);
        return admin;
    }
    
    
    @Bean
    @DependsOn("rabbitAdmin")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        
        factory.setMissingQueuesFatal(false);
        factory.setAutoStartup(true);
        
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
    
    
    
    @Bean
    public Queue fanoutQueue() {
        return QueueBuilder.durable(fanoutQueueName)
                .withArgument("x-message-ttl", 60000) 
                .build();
    }
    
    @Bean
    public TopicExchange fanoutExchange() {
        return new TopicExchange(FANOUT_EXCHANGE);
    }
    
    @Bean
    public Binding fanoutBinding() {
        return BindingBuilder
                .bind(fanoutQueue())
                .to(fanoutExchange())
                .with(FANOUT_ROUTING_KEY);
    }
    
    
    
    @Bean
    public Queue timelineQueue() {
        return QueueBuilder.durable(timelineQueueName)
                .withArgument("x-message-ttl", 300000) 
                .build();
    }
    
    @Bean
    public TopicExchange timelineExchange() {
        return new TopicExchange(TIMELINE_EXCHANGE);
    }
    
    @Bean
    public Binding timelineBinding() {
        return BindingBuilder
                .bind(timelineQueue())
                .to(timelineExchange())
                .with(TIMELINE_ROUTING_KEY);
    }
    
    
    
    @Bean
    public Queue notificationsQueue() {
        return QueueBuilder.durable(notificationsQueueName)
                .withArgument("x-message-ttl", 86400000) 
                .build();
    }
    
    @Bean
    public TopicExchange notificationsExchange() {
        return new TopicExchange(NOTIFICATIONS_EXCHANGE);
    }
    
    @Bean
    public Binding notificationsBinding() {
        return BindingBuilder
                .bind(notificationsQueue())
                .to(notificationsExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }
    
    
    
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("microblog.dlq").build();
    }
    
    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange("microblog.dlx");
    }
    
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("dlq.*");
    }
} 