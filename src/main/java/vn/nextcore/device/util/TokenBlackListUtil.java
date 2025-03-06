package vn.nextcore.device.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TokenBlackListUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    private final String BLACK_LIST = "BLACKLIST:";

    public TokenBlackListUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isTokenBlacklisted(String token) {
        String key = BLACK_LIST + token;
        return redisTemplate.opsForValue().get(key) != null;
    }

    public void addBlacklistToken(String token, long expiry) {
        String key = BLACK_LIST + token;
        redisTemplate.opsForValue().set(key, "true", expiry, TimeUnit.MILLISECONDS);
    }
}
