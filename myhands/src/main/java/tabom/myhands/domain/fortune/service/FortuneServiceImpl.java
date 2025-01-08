package tabom.myhands.domain.fortune.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tabom.myhands.common.infra.redis.RedisService;
import tabom.myhands.common.properties.ConstProperties;
import tabom.myhands.domain.fortune.entity.Fortune;
import tabom.myhands.domain.fortune.repository.FortuneRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class FortuneServiceImpl implements FortuneService {
    private final FortuneRepository fortuneRepository;
    private final RedisService redisService;
    private final ConstProperties constProperties;


    @Override
    public String getFortune(int userId) {
        String fortune = redisService.getStringValue(Integer.toString(userId));
        int fortuneId = 0;

        if(fortune != null) {
            fortuneId = Integer.parseInt(fortune);
        }  else {
            fortuneId = (int) (Math.random() * constProperties.getFortuneSize()) + 1;
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
            long expireTime = ChronoUnit.SECONDS.between(now, midnight);

            redisService.setStringValueAndExpire(Integer.toString(userId), Integer.toString(fortuneId), expireTime);
        }

        Fortune getFortune = fortuneRepository.findByFortuneId(fortuneId);
        return getFortune.getContent();
    }
}
