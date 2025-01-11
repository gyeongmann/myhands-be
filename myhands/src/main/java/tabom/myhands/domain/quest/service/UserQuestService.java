package tabom.myhands.domain.quest.service;

import tabom.myhands.domain.quest.dto.QuestResponse;
import tabom.myhands.domain.quest.dto.UserQuestRequest;
import tabom.myhands.domain.quest.dto.UserQuestResponse;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.quest.entity.UserQuest;
import tabom.myhands.domain.user.entity.User;

import java.util.List;

public interface UserQuestService {
    
    List<UserQuest> getUserQuests(User user);

    UserQuest createUserQuest(UserQuestRequest.Create request);

    List<UserQuestResponse> createJobQuest(Integer departmentId, Quest weekCountJobQuest);

    List<QuestResponse> getQuests(Long userId);

    List<QuestResponse> getCompletedQuest(Long userId);
}
