package org.lavender.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "feature.cache.caffeine", name = "enabled", havingValue = "true")
public class CaffeineCacheConfig {
    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = Caffeine.newBuilder().build().;
        return cacheManager;
    }
    Cache
}
