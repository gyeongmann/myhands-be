package tabom.myhands.common.infra.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public void setStringValueAndExpire(String key, String value, long expireTime) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(key, value, expireTime, TimeUnit.SECONDS);
    }

    public String getStringValue(String key) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    public void deleteRefreshToken(Long userId, boolean isAdmin) {
        String redisKey = isAdmin ? "admin:" + userId : "user:" + userId;
        redisTemplate.delete(redisKey);
    }

    public boolean isBlacklisted(String accessToken) {
        return redisTemplate.hasKey(getBlacklistKey(accessToken));
    }

    private String getKey(Long userId) {
        return "refreshToken:" + userId;
    }

    private String getBlacklistKey(String token) {
        return "blacklist:" + token;
    }

}
