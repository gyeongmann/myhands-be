package tabom.myhands.domain.quest.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public QuestResponse.QuestPage getCompletedQuest(HttpServletRequest request, int page, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        User user = getUser(request);
        Page<Quest> userQuestsPage = userQuestRepository.findAllByUserId(user, pageRequest);
        List<QuestResponse.QuestResponseTimeFormat> quests = getQuestList(userQuestsPage.getContent(), user.getName());
        return QuestResponse.QuestPage.from(quests, !userQuestsPage.isLast(), userQuestsPage.getTotalPages(), userQuestsPage.getTotalElements(), userQuestsPage.getNumber());
    }

    private static String calculateTimeAgo(LocalDateTime createdAt, LocalDateTime now) {
        Duration duration = Duration.between(createdAt, now);

        System.out.println("now = " + now);
        if (duration.toSeconds() < 60) {
            return duration.toSeconds() + "초 전";
        } else if (duration.toMinutes() < 60) {
            return duration.toMinutes() + "분 전";
        } else if (duration.toHours() < 24) {
            return duration.toHours() + "시간 전";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            return createdAt.format(formatter); // 그 외는 날짜로 표시
        }
    }

    private List<QuestResponse.QuestResponseTimeFormat> getQuestList(List<Quest> quests, String userName) {
        List<QuestResponse.QuestResponseTimeFormat> list = new ArrayList<>();
        for (Quest quest : quests) {
            String name = quest.getName();
            if (name.endsWith("주차")) {
                name = name.substring(0, name.length() - 4);
            } else if (name.endsWith(userName)) {
                name = name.split(" \\|")[0];
            }
            // TODO 임시 시간 설정
            String timeFormat = calculateTimeAgo(quest.getCompletedAt(), LocalDateTime.now().withMonth(1).withDayOfMonth(19).withHour(0).withMinute(5).withSecond(10));
            String grade = quest.getGrade();
            if (quest.getQuestType().equals("hr") || quest.getQuestType().equals("company")) {
                grade = "OTHER";
            }
            list.add(QuestResponse.QuestResponseTimeFormat.from(quest, name, grade, timeFormat));
        }
        return list;
    }

    private User getUser(Long userId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            throw new UserApiException(UserErrorCode.USER_ID_NOT_FOUND);
        }
        return optionalUser.get();
    }

    private User getUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return getUser(userId);
    }

    private Quest getQuest(Long questId) {
        Optional<Quest> optionalQuest = questRepository.findByQuestId(questId);
        if (optionalQuest.isEmpty()) {
            throw new IllegalArgumentException("Quest not found");
        }
        return optionalQuest.get();
    }
}
