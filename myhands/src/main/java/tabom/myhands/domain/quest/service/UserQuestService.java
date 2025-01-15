package tabom.myhands.domain.quest.service;

import jakarta.servlet.http.HttpServletRequest;
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

    List<UserQuestResponse> createJobUserQuest(String departmentName, Integer jobGroup, Quest weekCountJobQuest);

    List<QuestResponse> getQuests(Long userId);

    QuestResponse.QuestPage getCompletedQuest(HttpServletRequest request, int page, int size);
}
