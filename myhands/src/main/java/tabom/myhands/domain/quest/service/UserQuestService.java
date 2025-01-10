package tabom.myhands.domain.quest.service;

import tabom.myhands.domain.quest.dto.UserQuestRequest;
import tabom.myhands.domain.quest.entity.UserQuest;
import tabom.myhands.domain.user.entity.User;

import java.util.List;

public interface UserQuestService {
    
    List<UserQuest> getUserQuests(User user);

    UserQuest createUserQuest(UserQuestRequest.Create request);

}
