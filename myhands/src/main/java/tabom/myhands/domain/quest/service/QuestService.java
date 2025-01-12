package tabom.myhands.domain.quest.service;

import tabom.myhands.domain.quest.dto.QuestRequest;
import tabom.myhands.domain.quest.dto.QuestResponse;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.user.entity.User;

import java.util.List;

public interface QuestService {
    
    Quest createQuest(QuestRequest.Create request);

    Quest updateQuest(QuestRequest.Complete request);

    List<Quest> getUserQuestList(User user);

    Quest createWeekCountJobQuest(QuestRequest.JobQuest request);

    QuestResponse getWeekCountJobQuest(QuestRequest.JobQuest request);

    QuestResponse updateWeekCountJobQuest(QuestRequest.UpdateJobQuest request);

    QuestResponse getLeaderQuest(QuestRequest.LeaderQuest request);

    QuestResponse updateLeaderQuest(QuestRequest.UpdateLeaderQuest request);
}
