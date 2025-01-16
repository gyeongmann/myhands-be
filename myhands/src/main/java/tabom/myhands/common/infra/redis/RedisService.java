package tabom.myhands.common.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
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

    public void addToBlacklist(String accessToken, long expirationTime) {
        redisTemplate.opsForValue().set(getBlacklistKey(accessToken), "true", expirationTime, TimeUnit.MILLISECONDS);
    }

    private String getBlacklistKey(String token) {
        return "blacklist:" + token;
    }

    public void updateDepartmentExp(int departmentId, int preExp, int newExp) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String key = "department:" + departmentId + ":exp";

        String beforeExp = valueOps.get(key);
        int totalExp = (beforeExp != null ? Integer.parseInt(beforeExp) : 0) - preExp + newExp;

        valueOps.set(key, String.valueOf(totalExp));
    }

    @Scheduled(cron = "0 0 0 ? * MON", zone = "Asia/Seoul")
    private void resetWeeklyDepartmentExp() {
        Set<String> keys = redisTemplate.keys("department:*:exp");

        if (keys != null) {
            for (String key : keys) {
                redisTemplate.opsForValue().set(key, "0");
            }
        }
    }
}
