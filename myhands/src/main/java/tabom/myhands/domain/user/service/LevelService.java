package tabom.myhands.domain.user.service;

public interface LevelService {
    String calculateLevel(int currentExp);
    String getLowestLevel(String group);
}
