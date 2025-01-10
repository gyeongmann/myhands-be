package tabom.myhands.domain.quest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tabom.myhands.domain.quest.dto.UserQuestRequest;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.quest.entity.UserQuest;
import tabom.myhands.domain.quest.repository.QuestRepository;
import tabom.myhands.domain.quest.repository.UserQuestRepository;
import tabom.myhands.domain.user.entity.User;
import tabom.myhands.domain.user.repository.UserRepository;
import tabom.myhands.error.errorcode.UserErrorCode;
import tabom.myhands.error.exception.UserApiException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQuestServiceImpl implements UserQuestService {

    private final UserRepository userRepository;
    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;

    @Override
    public List<UserQuest> getUserQuests(User user) {
        return userQuestRepository.findByUser(user);
    }

    @Override
    @Transactional
    public UserQuest createUserQuest(UserQuestRequest.Create request) {
        System.out.println(request.getQuestId() + " " + request.getQuestId());
        Optional<User> optionalUser = userRepository.findByUserId(request.getUserId());
        Optional<Quest> optionalQuest = questRepository.findByQuestId(request.getQuestId());

        if (optionalUser.isEmpty()) {
            throw new UserApiException(UserErrorCode.USER_ID_NOT_FOUND);
        }

        if (optionalQuest.isEmpty()) {
            throw new IllegalArgumentException("No quest found");
        }

        User user = optionalUser.get();
        Quest quest = optionalQuest.get();
        UserQuest userQuest = UserQuest.build(user, quest);

        return userQuestRepository.save(userQuest);
    }
}
