package com.PPPL.backend.config.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimiterRedisConfig {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final int AI_MAX_REQUESTS = 15;
    private static final long AI_WINDOW_SECONDS = 120;

    private static final int AUTH_MAX_ATTEMPTS = 5;
    private static final long AUTH_WINDOW_SECONDS = 900;

    public RateLimiterRedisConfig(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // AI Rate Limiter
    /**
     * Check if AI request is allowed for a given admin ID
     */
    public boolean allowAiRequest(Integer adminId) {
        String key = "rate_limit:ai:admin:" + adminId;
        return checkAndIncrement(key, AI_WINDOW_SECONDS, AI_MAX_REQUESTS);
    }

    public long getAiRemainingTokens(Integer adminId) {
        String key = "rate_limit:ai:admin:" + adminId;
        return getRemainingRequests(key, AI_MAX_REQUESTS);
    }

    public long getAiTimeUntilReset(Integer adminId) {
        String key = "rate_limit:ai:admin:" + adminId;
        return getTimeUntilReset(key);
    }

    public void clearAiRateLimit(Integer adminId) {
        redisTemplate.delete("rate_limit:ai:admin:" + adminId);
    }

    public int getAiMaxRequests() {
        return AI_MAX_REQUESTS;
    }

    public long getAiWindowSeconds() {
        return AI_WINDOW_SECONDS;
    }

    // Auth Rate Limiter
    /**
     * Check if auth attempt is allowed for a given IP address
     */
    public boolean allowAuthAttempt(String ipAddress) {
        String key = "rate_limit:auth:ip:" + ipAddress;
        return checkAndIncrement(key, AUTH_WINDOW_SECONDS, AUTH_MAX_ATTEMPTS);
    }

    public long getAuthRemainingAttempts(String ipAddress) {
        String key = "rate_limit:auth:ip:" + ipAddress;
        return getRemainingRequests(key, AUTH_MAX_ATTEMPTS);
    }

    public long getAuthTimeUntilReset(String ipAddress) {
        String key = "rate_limit:auth:ip:" + ipAddress;
        return getTimeUntilReset(key);
    }

    public void clearAuthRateLimit(String ipAddress) {
        redisTemplate.delete("rate_limit:auth:ip:" + ipAddress);
    }

    public int getAuthMaxAttempts() {
        return AUTH_MAX_ATTEMPTS;
    }

    public long getAuthWindowSeconds() {
        return AUTH_WINDOW_SECONDS;
    }

    // Private helper methods
    /**
     * Increment request count and check if within limit
     */
    private boolean checkAndIncrement(String key, long windowSeconds, int maxRequests) {
        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount == null) {
            currentCount = 0L;
        }

        // Set expiration only when the key is first created
        if (currentCount == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }

        return currentCount <= maxRequests;
    }

    /**
     * Get remaining requests
     */
    private long getRemainingRequests(String key, int maxRequests) {
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return maxRequests;
        }

        long currentCount;
        if (value instanceof Integer) {
            currentCount = ((Integer) value).longValue();
        } else if (value instanceof Long) {
            currentCount = (Long) value;
        } else {
            currentCount = Long.parseLong(value.toString());
        }

        return Math.max(0, maxRequests - currentCount);
    }

    /**
     * Get time until rate limit resets in seconds
     */
    private long getTimeUntilReset(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        if (ttl == null || ttl < 0) {
            return 0;
        }

        return ttl;
    }
}