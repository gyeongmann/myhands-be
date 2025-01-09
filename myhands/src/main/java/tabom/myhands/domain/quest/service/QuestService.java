package tabom.myhands.domain.quest.service;

import tabom.myhands.domain.quest.dto.QuestRequest;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.user.entity.User;

import java.util.List;

public interface QuestService {
    
    Quest createQuest(Quest quest);

    Quest updateQuest(QuestRequest.CompleteQuest request);

    List<Quest> getUserQuestList(User user);
}
