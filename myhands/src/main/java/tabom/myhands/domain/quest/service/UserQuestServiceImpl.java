package tabom.myhands.domain.quest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tabom.myhands.domain.quest.dto.QuestResponse;
import tabom.myhands.domain.quest.dto.UserQuestRequest;
import tabom.myhands.domain.quest.dto.UserQuestResponse;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.quest.entity.UserQuest;
import tabom.myhands.domain.quest.repository.QuestRepository;
import tabom.myhands.domain.quest.repository.UserQuestRepository;
import tabom.myhands.domain.user.entity.Department;
import tabom.myhands.domain.user.entity.User;
import tabom.myhands.domain.user.repository.DepartmentRepository;
import tabom.myhands.domain.user.repository.UserRepository;
import tabom.myhands.error.errorcode.UserErrorCode;
import tabom.myhands.error.exception.UserApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQuestServiceImpl implements UserQuestService {

    private final UserRepository userRepository;
    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;
    private final DepartmentRepository departmentRepository;

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

    @Override
    @Transactional
    public List<UserQuestResponse> createJobQuest(Integer departmentId, Quest weekCountJobQuest) {
        Optional<Department> optionalDepartment = departmentRepository.findDepartmentByDepartmentId(departmentId);
        if (optionalDepartment.isEmpty()) {
            throw new IllegalArgumentException("No department found");
        }

        List<UserQuestResponse> response = new ArrayList<>();
        List<User> usersByDepartment = userRepository.findUsersByDepartment(optionalDepartment.get());
        for (User user : usersByDepartment) {
            UserQuest userQuest = createUserQuest(new UserQuestRequest.Create(user.getUserId(), weekCountJobQuest.getQuestId()));
            response.add(UserQuestResponse.from(userQuest));
        }
        return response;
    }

    @Override
    public List<QuestResponse> getQuests(Long userId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            throw new UserApiException(UserErrorCode.USER_ID_NOT_FOUND);
        }

        User user = optionalUser.get();
        List<UserQuest> userQuests = getUserQuests(user);
        List<QuestResponse> quests = new ArrayList<>();
        for (UserQuest userQuest : userQuests) {
            quests.add(QuestResponse.from(userQuest.getQuest()));
        }

        return quests;
    }

    @Override
    public List<QuestResponse> getCompletedQuest(Long userId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            throw new UserApiException(UserErrorCode.USER_ID_NOT_FOUND);
        }

        User user = optionalUser.get();
        List<QuestResponse> completedQuests = new ArrayList<>();
        List<UserQuest> userQuests = userQuestRepository.findByUser(user);
        for (UserQuest userQuest : userQuests) {
            Quest quest = userQuest.getQuest();
            if (quest.getIsCompleted()) {
                completedQuests.add(QuestResponse.from(quest));
            }
        }
        return completedQuests;
    }
}
