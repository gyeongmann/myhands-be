package tabom.myhands.domain.quest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.quest.entity.UserQuest;
import tabom.myhands.domain.quest.repository.UserQuestRepository;
import tabom.myhands.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpServiceImpl implements ExpService {

    private final UserQuestRepository userQuestRepository;

    @Override
    public Integer getCurrentExp(User user) {
        List<UserQuest> userQuests = userQuestRepository.findByUserWithQuest(user);

        Integer totalExp = 0;
        for (UserQuest userQuest : userQuests) {
            Quest quest = userQuest.getQuest();
            Integer expAmount = quest.getExpAmount();
            totalExp += expAmount;
        }
        return totalExp;
    }

    @Override
    public Integer getThisYearExp(User user) {
        List<UserQuest> userQuests = userQuestRepository.findByUserAndQuestCompletedYear(user, 2025);

        Integer totalExp = 0;
        for (UserQuest userQuest : userQuests) {
            Quest quest = userQuest.getQuest();
            Integer expAmount = quest.getExpAmount();
            totalExp += expAmount;
        }
        return totalExp;
    }

    @Override
    public Integer getLastYearExp(User user) {
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        List<UserQuest> userQuests = userQuestRepository.findByUserAndQuestCompletedBeforeDate(user, endDate);

        Integer totalExp = 0;
        for (UserQuest userQuest : userQuests) {
            Quest quest = userQuest.getQuest();
            Integer expAmount = quest.getExpAmount();
            totalExp += expAmount;
        }
        return totalExp;
    }

    @Override
    public Integer getYearExp(User user, Integer year) {
        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year + 1, 1, 1, 23, 59, 59).minusDays(1);
        List<Quest> quests = userQuestRepository.findQuestsBetweenDates(user, startDate, endDate);

        Integer totalExp = 0;
        for (Quest quest : quests) {
            totalExp += quest.getExpAmount();
        }
        return totalExp;
    }
}
