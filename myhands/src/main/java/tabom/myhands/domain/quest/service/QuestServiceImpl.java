package tabom.myhands.domain.quest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tabom.myhands.domain.quest.dto.QuestRequest;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.quest.entity.UserQuest;
import tabom.myhands.domain.quest.repository.QuestRepository;
import tabom.myhands.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestServiceImpl implements QuestService {

    static final LocalDateTime START_DATE_TIME = LocalDateTime.of(2025, 1, 5, 0, 0);

    private final QuestRepository questRepository;
    private final UserQuestService userQuestService;

    @Override
    @Transactional
    public Quest createQuest(QuestRequest.Create request) {
        Quest quest = Quest.build(request.getQuestType(), request.getName());
        return questRepository.save(quest);
    }

    @Override
    @Transactional
    public Quest createWeekCountJobQuest(Integer weekCount) {
        String questName = String.format("직무별 퀘스트 %d주차", weekCount);
        Quest quest = createQuest(new QuestRequest.Create("job", questName));
        LocalDateTime completedDateTime = START_DATE_TIME.plusWeeks(weekCount-1);
        updateQuest(new QuestRequest.Complete(quest.getQuestId(), "", 0, false, completedDateTime));
        return quest;
    }

    @Override
    @Transactional
    public Quest updateQuest(QuestRequest.Complete request) {
        Optional<Quest> optionalQuest = questRepository.findByQuestId(request.getQuestId());

        if (optionalQuest.isEmpty()) {
            throw  new IllegalArgumentException("Quest not found");
        }
        Quest quest = optionalQuest.get();

        quest.update(request.getGrade(), request.getExpAmount(), request.getIsCompleted(), request.getCompletedAt());
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

}
