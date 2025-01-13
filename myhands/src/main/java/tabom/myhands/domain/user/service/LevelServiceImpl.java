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

    public String calculateLevel(String group, int currentExp) {
        Map<String, Integer> groupLevels = levelProperties.getLevels().get(group);
        if (groupLevels == null) {
            throw new UserApiException(UserErrorCode.INVALID_LEVEL_VALUE); // 잘못된 그룹 입력 예외 처리
        }

        String currentLevel = "Unknown";

        for (Map.Entry<String, Integer> entry : groupLevels.entrySet()) {
            if (currentExp >= entry.getValue()) {
                currentLevel = entry.getKey(); // 경험치가 해당 레벨 이상이면 갱신
            } else {
                break; // 현재 경험치보다 높은 레벨이 나오면 종료
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

    public Map.Entry<String, Integer> getNextLevel(String group, String currentLevel) {
        // 그룹의 레벨 데이터 가져오기
        Map<String, Integer> groupLevels = levelProperties.getLevels().get(group);
        if (groupLevels == null) {
            throw new UserApiException(UserErrorCode.INVALID_LEVEL_VALUE);
        }

        boolean foundCurrent = false;
        for (Map.Entry<String, Integer> entry : groupLevels.entrySet()) {
            if (foundCurrent) {
                return entry; // 현재 레벨 이후의 첫 번째 레벨 반환
            }
            if (entry.getKey().equals(currentLevel)) {
                foundCurrent = true; // 현재 레벨을 찾으면 플래그 설정
            }
        }

        return null; // 다음 레벨이 없는 경우 null 반환
    }
}
