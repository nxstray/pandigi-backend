package com.PPPL.backend.config.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimiterRedisConfig {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Rate limit: 15 requests per 2 minutes
    private static final int MAX_REQUESTS = 15;
    private static final long WINDOW_SECONDS = 120; // 2 minutes
    
    public RateLimiterRedisConfig(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * Check if request is allowed for admin
     */
    public boolean allowRequest(Integer adminId) {
        String key = getRateLimitKey(adminId);
        
        // Get current count
        Long currentCount = redisTemplate.opsForValue().increment(key);
        
        if (currentCount == null) {
            currentCount = 0L;
        }
        
        // Set expiration on first request
        if (currentCount == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(WINDOW_SECONDS));
        }
        
        // Check if exceeded
        return currentCount <= MAX_REQUESTS;
    }
    
    /**
     * Get remaining tokens for admin
     */
    public long getRemainingTokens(Integer adminId) {
        String key = getRateLimitKey(adminId);
        Object value = redisTemplate.opsForValue().get(key);
        
        if (value == null) {
            return MAX_REQUESTS;
        }
        
        // Handle Integer or Long
        long currentCount;
        if (value instanceof Integer) {
            currentCount = ((Integer) value).longValue();
        } else if (value instanceof Long) {
            currentCount = (Long) value;
        } else {
            currentCount = 0L;
        }
        
        long remaining = MAX_REQUESTS - currentCount;
        return Math.max(0, remaining);
    }
    
    /**
     * Get time until reset (in seconds)
     */
    public long getTimeUntilReset(Integer adminId) {
        String key = getRateLimitKey(adminId);
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        
        if (ttl == null || ttl < 0) {
            return 0;
        }
        
        return ttl;
    }
    
    /**
     * Clear rate limit for admin (for testing/reset)
     */
    public void clearRateLimit(Integer adminId) {
        String key = getRateLimitKey(adminId);
        redisTemplate.delete(key);
    }
    
    /**
     * Get max requests limit
     */
    public int getMaxRequests() {
        return MAX_REQUESTS;
    }
    
    /**
     * Get window duration in seconds
     */
    public long getWindowSeconds() {
        return WINDOW_SECONDS;
    }
    
    /**
     * Generate Redis key for rate limiting
     */
    private String getRateLimitKey(Integer adminId) {
        return "rate_limit:admin:" + adminId;
    }
}