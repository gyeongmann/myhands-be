package tabom.myhands.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import tabom.myhands.common.properties.LevelProperties;
import tabom.myhands.error.errorcode.UserErrorCode;
import tabom.myhands.error.exception.UserApiException;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {

    private final LevelProperties levelProperties;

    public String calculateLevel(int currentExp) {
        String currentLevel = "Unknown";

        for (Map<String, Integer> groupLevels : levelProperties.getLevels().values()) {
            for (Map.Entry<String, Integer> entry : groupLevels.entrySet()) {
                if (currentExp >= entry.getValue()) {
                    currentLevel = entry.getKey(); // 경험치가 해당 레벨 이상이면 갱신
                } else {
                    break; // 현재 경험치보다 높은 레벨이 나오면 종료
                }
            }
        }
        return currentLevel;
    }

    public String getLowestLevel(String group) {
        // 특정 그룹의 최하위 레벨 계산
        Map<String, Integer> groupLevels = levelProperties.getLevels().get(group);
        if (groupLevels == null) {
            throw new UserApiException(UserErrorCode.INVALID_LEVEL_VALUE);
        }
        return groupLevels.keySet().stream()
                .sorted()
                .findFirst() // 최하위 레벨 선택
                .orElseThrow(() -> new UserApiException(UserErrorCode.INVALID_LEVEL_VALUE));
    }
}
