package com.userservice.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * This method is used to log when a user exceeds the rate limit.
 * Marks this class as a Spring service
 */
@Service
public class RedisLoggingService {
    private final StringRedisTemplate redisTemplate;

    public RedisLoggingService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * This method is used to log when a user exceeds the rate limit.
     * @param emailId - user
     */
    public void logRateLimit(String emailId) {
        String key = "rateLimit:" + emailId;
        String value = "User exceeded limit at: " + LocalDateTime.now();
        redisTemplate.opsForList().rightPush(key, value);
        redisTemplate.expire(key, Duration.ofDays(7));
    }

}
