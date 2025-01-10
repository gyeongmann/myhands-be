package tabom.myhands.domain.user.service;

public interface LevelService {
    String calculateLevel(String group, int currentExp);
    String getLowestLevel(String group);
}
