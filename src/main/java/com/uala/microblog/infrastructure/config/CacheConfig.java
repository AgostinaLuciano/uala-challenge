package com.uala.microblog.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);
    
    
    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:password123}")
    private String redisPassword;

    @Value("${spring.redis.database:0}")
    private int redisDatabase;

    @Value("${spring.redis.timeout:2000ms}")
    private Duration redisTimeout;

    @Value("${spring.redis.connect-timeout:1500ms}")
    private Duration redisConnectTimeout;
    
    /**
     * Jackson serializer personalizado con soporte para fechas Java 8
     */
    private GenericJackson2JsonRedisSerializer createJsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    /**
     * Configuración de conexión Redis con diagnóstico avanzado
     * Este bean reemplaza la configuración automática de Spring Boot
     */
    @Bean
    @ConditionalOnProperty(
        name = "spring.cache.type",
        havingValue = "redis",
        matchIfMissing = false
    )
    public LettuceConnectionFactory redisConnectionFactory() {
        logger.info("🔧 Configurando conexión Redis consolidada con diagnóstico avanzado");
        logger.info("📡 Redis - Host: {}, Port: {}, Database: {}", redisHost, redisPort, redisDatabase);

        try {
            
            ClientResources clientResources = DefaultClientResources.builder()
                    .ioThreadPoolSize(4)
                    .computationThreadPoolSize(4)
                    .build();

            SocketOptions socketOptions = SocketOptions.builder()
                    .connectTimeout(redisConnectTimeout)
                    .keepAlive(true)
                    .tcpNoDelay(true)
                    .build();

            TimeoutOptions timeoutOptions = TimeoutOptions.builder()
                    .fixedTimeout(redisTimeout)
                    .build();

            ClientOptions clientOptions = ClientOptions.builder()
                    .socketOptions(socketOptions)
                    .timeoutOptions(timeoutOptions)
                    .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                    .autoReconnect(true)
                    .cancelCommandsOnReconnectFailure(false)
                    .suspendReconnectOnProtocolFailure(false)
                    .build();

            LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                    .clientOptions(clientOptions)
                    .clientResources(clientResources)
                    .commandTimeout(redisTimeout)
                    .shutdownTimeout(Duration.ofMillis(2000))
                    .build();

            
            RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration();
            serverConfig.setHostName(redisHost);
            serverConfig.setPort(redisPort);
            serverConfig.setDatabase(redisDatabase);
            if (redisPassword != null && !redisPassword.isEmpty()) {
                serverConfig.setPassword(redisPassword);
            }

            LettuceConnectionFactory factory = new LettuceConnectionFactory(serverConfig, clientConfig);
            factory.setValidateConnection(true);
            factory.setShareNativeConnection(true);

            logger.info("✅ Redis ConnectionFactory configurado exitosamente");
            return factory;

        } catch (Exception e) {
            logger.error("❌ Error al configurar Redis ConnectionFactory: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to configure Redis connection", e);
        }
    }

    /**
     * Configuración simplificada de cache manager con Redis
     * Con mejor diagnóstico de errores
     */
    @Bean
    @Primary
    @ConditionalOnProperty(
        name = "spring.cache.type", 
        havingValue = "redis", 
        matchIfMissing = false
    )
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        logger.info("🔧 Configurando Redis Cache Manager con diagnóstico mejorado");
        
        try {
            
            logger.info("🔍 Verificando conexión Redis para cache...");
            String pong = redisConnectionFactory.getConnection().ping();
            logger.info("✅ Redis connection successful for cache - Response: {}", pong);
            
            
            RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(30)) 
                    .serializeKeysWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
                            .fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
                            .fromSerializer(createJsonRedisSerializer()))
                    .disableCachingNullValues(); 

            
            Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
            
            
            cacheConfigurations.put("push-timeline", defaultConfig
                    .entryTtl(Duration.ofDays(7))); 
                    
            
            cacheConfigurations.put("timeline-fallback", defaultConfig
                    .entryTtl(Duration.ofMinutes(15))); 
            
            
            cacheConfigurations.put("users", defaultConfig
                    .entryTtl(Duration.ofHours(2))); 
            
            
            cacheConfigurations.put("follows", defaultConfig
                    .entryTtl(Duration.ofMinutes(30))); 
                    
            
            cacheConfigurations.put("stats", defaultConfig
                    .entryTtl(Duration.ofMinutes(15))); 

            logger.info("📊 Configurando {} caches específicos", cacheConfigurations.size());
            cacheConfigurations.forEach((name, config) -> 
                logger.info("   - Cache '{}' con TTL: {}", name, config.getTtl())
            );

            RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(defaultConfig)
                    .withInitialCacheConfigurations(cacheConfigurations)
                    .transactionAware() 
                    .build();
                    
            logger.info("✅ Redis Cache Manager configurado exitosamente");
            return cacheManager;
                    
        } catch (Exception e) {
            logger.error("❌ FALLO CRÍTICO: No se pudo conectar a Redis para cache: {}", e.getMessage());
            logger.error("🔧 Diagnóstico de conexión Redis:");
            logger.error("   - Verificar que Redis esté ejecutándose");
            logger.error("   - Verificar configuración de host/puerto en application.yml");
            logger.error("   - Verificar credenciales de autenticación");
            logger.error("   - Revisar logs de RedisConfig para más detalles");
            
            
            throw new RuntimeException("Redis es requerido para el inicio de la aplicación. Verificar que Redis esté corriendo y accesible.", e);
        }
    }

    /**
     * RedisTemplate optimizado para operaciones manuales de cache
     */
    @Bean
    @ConditionalOnProperty(
        name = "spring.cache.type", 
        havingValue = "redis", 
        matchIfMissing = false
    )
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        logger.info("🔧 Configurando RedisTemplate optimizado");
        
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        
        
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = createJsonRedisSerializer();
        
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.setDefaultSerializer(jsonSerializer);
        
        
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        
        logger.info("✅ RedisTemplate configurado exitosamente");
        return template;
    }

    /**
     * Health Indicator personalizado para Redis con diagnóstico detallado
     */
    @Bean
    @ConditionalOnProperty(
        name = "spring.cache.type", 
        havingValue = "redis", 
        matchIfMissing = false
    )
    public HealthIndicator redisHealthIndicator(RedisConnectionFactory connectionFactory) {
        return new HealthIndicator() {
            @Override
            public Health health() {
                try {
                    
                    logger.debug("🔍 Ejecutando health check de Redis...");
                    String pong = connectionFactory.getConnection().ping();
                    
                    if ("PONG".equals(pong)) {
                        logger.debug("✅ Redis health check exitoso");
                        return Health.up()
                                .withDetail("status", "Connected")
                                .withDetail("host", redisHost)
                                .withDetail("port", redisPort)
                                .withDetail("database", redisDatabase)
                                .withDetail("response", pong)
                                .withDetail("timestamp", System.currentTimeMillis())
                                .build();
                    } else {
                        logger.warn("⚠️ Redis health check - respuesta inesperada: {}", pong);
                        return Health.down()
                                .withDetail("status", "Unexpected response")
                                .withDetail("response", pong)
                                .withDetail("host", redisHost)
                                .withDetail("port", redisPort)
                                .build();
                    }
                } catch (Exception e) {
                    logger.error("❌ Redis health check falló: {}", e.getMessage());
                    return Health.down()
                            .withDetail("status", "Connection failed")
                            .withDetail("error", e.getMessage())
                            .withDetail("host", redisHost)
                            .withDetail("port", redisPort)
                            .withDetail("database", redisDatabase)
                            .withDetail("diagnosis", "Verificar que Redis esté corriendo y accesible")
                            .build();
                }
            }
        };
    }

    /**
     * Bean para verificar conexión Redis durante startup
     */
    @Bean
    @ConditionalOnProperty(
        name = "spring.cache.type", 
        havingValue = "redis", 
        matchIfMissing = false
    )
    public RedisConnectionTester redisConnectionTester(RedisConnectionFactory connectionFactory) {
        return new RedisConnectionTester(connectionFactory);
    }

    /**
     * Clase para testing de conexión Redis en startup con diagnóstico detallado
     */
    public static class RedisConnectionTester {
        private static final Logger logger = LoggerFactory.getLogger(RedisConnectionTester.class);
        
        public RedisConnectionTester(RedisConnectionFactory connectionFactory) {
            testConnection(connectionFactory);
        }
        
        private void testConnection(RedisConnectionFactory connectionFactory) {
            logger.info("🔍 Iniciando verificación de conexión Redis en startup...");
            
            try {
                String pong = connectionFactory.getConnection().ping();
                if ("PONG".equals(pong)) {
                    logger.info("✅ CONEXIÓN REDIS EXITOSA - Servicio disponible y configurado correctamente");
                    logger.info("📊 Redis configurado en modo cache con conexión validada");
                } else {
                    logger.warn("⚠️ Redis respondió pero con respuesta inesperada: {}", pong);
                }
            } catch (Exception e) {
                logger.error("❌ FALLO EN CONEXIÓN REDIS durante startup: {}", e.getMessage());
                logger.error("🔧 Diagnóstico de problemas Redis:");
                logger.error("   1. ¿Está Redis ejecutándose? → docker-compose up -d redis");
                logger.error("   2. ¿Es correcta la configuración? → Revisar application.yml");
                logger.error("   3. ¿Hay problemas de red? → Verificar conectividad");
                logger.error("   4. ¿Son correctas las credenciales? → Verificar password");
                logger.error("💡 Para habilitar debug: agregar 'org.springframework.data.redis: DEBUG' en logging");
                
                
                logger.error("⚠️ La aplicación continuará pero el cache Redis no estará disponible");
            }
        }
    }
} 