package tabom.myhands.domain.quest.service;

import com.google.firebase.messaging.FirebaseMessagingException;
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

    QuestResponse updateWeekCountJobQuest(QuestRequest.UpdateJobQuest request) throws FirebaseMessagingException;

    QuestResponse getLeaderQuest(QuestRequest.LeaderQuest request);

    QuestResponse updateLeaderQuest(QuestRequest.UpdateLeaderQuest request) throws FirebaseMessagingException;

    QuestResponse getCompanyQuest(QuestRequest.CompanyQuest request);

    QuestResponse updateCompanyQuest(QuestRequest.UpdateCompanyQuest request) throws FirebaseMessagingException;

    QuestResponse getHRQuest(QuestRequest.HRQuest request);

    QuestResponse updateHRQuest(QuestRequest.UpdateHRQuest request) throws FirebaseMessagingException;
}
