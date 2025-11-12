package com.vincent_luracelli.clickup_google_calendar_agenda.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;

    private static final int CACHE_EVICT_TIME_OUT = 60_000;

    public void evictAllCaches() {
        try {
            cacheManager.getCacheNames()
                    .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        } catch (NullPointerException e) {
           log.error("Couldn't find cache by name: {}", e.getMessage());
        }
    }

    @Scheduled(initialDelay = CACHE_EVICT_TIME_OUT, fixedRate = CACHE_EVICT_TIME_OUT)
    public void evictAllСachesAtIntervals() {
        evictAllCaches();
    }
}
