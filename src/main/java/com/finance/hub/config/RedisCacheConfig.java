package com.finance.hub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheStatisticsCollector;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisCacheConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //enable statistics collection on the Redis writer
        RedisCacheWriter writer = RedisCacheWriter
                .nonLockingRedisCacheWriter(connectionFactory)
                .withStatisticsCollector(CacheStatisticsCollector.create());

        //Default TTL for all caches (if not overridden)
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) //default TTL
                .disableCachingNullValues();

        //Per-cache TTL Configuration
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        //TTL for getAllContacts
        cacheConfigs.put("contacts", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigs.put("financialAccounts", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigs.put("relationships", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .enableStatistics()
                .build();
    }
}
