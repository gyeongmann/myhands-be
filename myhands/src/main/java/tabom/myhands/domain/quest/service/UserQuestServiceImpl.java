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

import java.time.LocalDateTime;
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
    public List<UserQuestResponse> createJobUserQuest(String departmentName, Integer jobGroup, Quest weekCountJobQuest) {
        Optional<Department> optionalDepartment = departmentRepository.findDepartmentByName(departmentName);
        if (optionalDepartment.isEmpty()) {
            throw new IllegalArgumentException("No department found");
        }

        List<UserQuestResponse> response = new ArrayList<>();
        List<User> usersByDepartment = userRepository.findUsersByDepartmentAndJobGroup(optionalDepartment.get(), jobGroup);
        for (User user : usersByDepartment) {
            UserQuest userQuest = createUserQuest(new UserQuestRequest.Create(user.getUserId(), weekCountJobQuest.getQuestId()));
            response.add(UserQuestResponse.from(userQuest));
        }
        return response;
    }

    @Override
    public List<QuestResponse> getQuests(Long userId) {
        User user = getUser(userId);
        List<UserQuest> userQuests = getUserQuests(user);
        List<QuestResponse> quests = new ArrayList<>();
        for (UserQuest userQuest : userQuests) {
            quests.add(QuestResponse.from(userQuest.getQuest()));
        }

        return quests;
    }

    @Override
    public List<QuestResponse> getCompletedQuest(Long userId) {
        User user = getUser(userId);
        List<QuestResponse> completedQuests = new ArrayList<>();
        // TODO: 임시 날짜 설정
        List<Quest> quests = userQuestRepository.findQuestsByUserCompletedAtDESC(user, LocalDateTime.of(2025, 2, 1, 0, 0));
        for (Quest quest : quests) {
            completedQuests.add(QuestResponse.from(quest));
        }
        return completedQuests;
    }

    private User getUser(Long userId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            throw new UserApiException(UserErrorCode.USER_ID_NOT_FOUND);
        }
        return optionalUser.get();
    }
}
