package tabom.myhands.domain.user.service;

import java.util.Map;

public interface LevelService {
    String calculateLevel(String group, int currentExp);
    String getLowestLevel(String group);
    Map.Entry<String, Integer> getNextLevel(String group, String currentLevel);
}
