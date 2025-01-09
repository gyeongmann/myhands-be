package tabom.myhands.domain.quest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tabom.myhands.domain.quest.entity.UserQuest;
import tabom.myhands.domain.quest.repository.UserQuestRepository;
import tabom.myhands.domain.user.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQuestServiceImpl implements UserQuestService {

    private final UserQuestRepository userQuestRepository;

    @Override
    public List<UserQuest> getUserQuests(User user) {
        return userQuestRepository.findByUser(user);
    }
}
