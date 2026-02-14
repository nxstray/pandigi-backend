package com.PPPL.backend.controller.health;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/health")
public class HealthController {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public HealthController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @GetMapping("/redis")
    public String checkRedis() {
        try {
            // Test connection
            redisTemplate.opsForValue().set("health:check", "OK");
            String result = (String) redisTemplate.opsForValue().get("health:check");
            
            if ("OK".equals(result)) {
                return "Redis Connected Successfully!";
            } else {
                return "Redis Connection Failed - Invalid Response";
            }
        } catch (Exception e) {
            return "Redis Connection Failed: " + e.getMessage();
        }
    }
}