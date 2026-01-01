package com.finance.hub.config.redisconfig;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        //Basic config (Serialization + null handling)
        RedisCacheConfiguration baseConfig = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                );

        //Per-cache TTls
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        //Single Contact
        cacheConfigs.put("contacts", baseConfig.entryTtl(Duration.ofMinutes(30)));
        //Paginated List
        cacheConfigs.put("contacts_list", baseConfig.entryTtl(Duration.ofMinutes(3)));
        //Count
        cacheConfigs.put("contacts_count", baseConfig.entryTtl(Duration.ofMinutes(3)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(baseConfig.entryTtl(Duration.ofMinutes(10))) //Fallback
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    @Bean
    public CacheManagerCustomizer<RedisCacheManager> cacheManagerCustomizer() {
        return cacheManager -> {
            //Enables Micrometer to bind cache metrics
        };
    }

    //Hash based key generator for cleaner Redis keys
    @Bean("hashKeyGenerator")
    public KeyGenerator hashKeyGenerator() {
        return (target, method, params) -> {

            String raw = method.getName() + ":" + java.util.Arrays.deepToString(params);

            return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
        };
    }
}
