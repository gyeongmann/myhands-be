package tabom.myhands.domain.quest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tabom.myhands.domain.quest.dto.QuestRequest;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.quest.entity.QuestGrade;
import tabom.myhands.domain.quest.entity.UserQuest;
import tabom.myhands.domain.quest.repository.QuestRepository;
import tabom.myhands.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestServiceImpl implements QuestService {

    private final QuestRepository questRepository;
    private final UserQuestService userQuestService;

    @Override
    @Transactional
    public Quest createQuest(Quest quest) {
        return null;
    }

    @Override
    public Quest updateQuest(QuestRequest.CompleteQuest request) {
        Optional<Quest> optionalQuest = questRepository.findByQuestId(request.getQuestId());

        if (optionalQuest.isEmpty()) {
            throw  new IllegalArgumentException("Quest not found");
        }
        Quest quest = optionalQuest.get();
        QuestGrade grade = getQuestGrade(quest.getQuestType(), request.getGrade());

        quest.update(grade, request.getExpAmount(), request.getIsCompleted(), request.getCompletedAt());
        return quest;
    }

    @Override
    public List<Quest> getUserQuestList(User user) {
        List<UserQuest> userQuests = userQuestService.getUserQuests(user);
        List<Quest> quests = new ArrayList<>();
        for(UserQuest userQuest : userQuests) {
            quests.add(userQuest.getQuest());
        }
        return quests;
    }

    private QuestGrade getQuestGrade(String questType, String grade) {
        if (questType.equals("hr")) {
            if (grade.equals("S")) {
                return QuestGrade.HR_S;
            } else if (grade.equals("A")) {
                return QuestGrade.HR_A;
            } else if (grade.equals("B")) {
                return QuestGrade.HR_B;
            } else if (grade.equals("C")) {
                return QuestGrade.HR_C;
            } else if (grade.equals("D")) {
                return QuestGrade.HR_D;
            }
        } else if (questType.equals("job")) {
            if (grade.equals("MAX")) {
                return QuestGrade.JOB_MAX;
            } else if (grade.equals("MED")) {
                return QuestGrade.JOB_MED;
            } else {
                return QuestGrade.JOB_FAILED;
            }
        } else if (questType.equals("leader")) {
            if (grade.equals("MAX")) {
                return QuestGrade.LEADER_MAX;
            } else if (grade.equals("MED")) {
                return QuestGrade.LEADER_MED;
            } else {
                return QuestGrade.LEADER_FAILED;
            }
        } else if (questType.equals("company")) {
            return QuestGrade.COMPANY;
        }

        throw new IllegalArgumentException("Invalid quest type: " + questType);
    }
}
