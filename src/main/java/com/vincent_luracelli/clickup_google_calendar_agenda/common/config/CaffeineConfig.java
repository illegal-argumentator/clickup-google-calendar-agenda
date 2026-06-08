package com.vincent_luracelli.clickup_google_calendar_agenda.common.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
public class CaffeineConfig {

    @Bean
    public Cache<String, ReentrantLock> lockCache() {
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .expireAfterAccess(1, TimeUnit.HOURS)
                .build();
    }
}
