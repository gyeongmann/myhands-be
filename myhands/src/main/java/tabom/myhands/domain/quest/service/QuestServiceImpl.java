package tabom.myhands.domain.quest.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tabom.myhands.common.infra.redis.RedisService;
import tabom.myhands.domain.alarm.entity.Alarm;
import tabom.myhands.domain.alarm.repository.AlarmRepository;
import tabom.myhands.domain.alarm.service.AlarmService;
import tabom.myhands.domain.quest.dto.QuestRequest;
import tabom.myhands.domain.quest.dto.QuestResponse;
import tabom.myhands.domain.quest.dto.UserQuestRequest;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuestServiceImpl implements QuestService {

    static final LocalDateTime START_DATE_TIME = LocalDateTime.of(2025, 1, 5, 0, 0);

    private final QuestRepository questRepository;
    private final UserQuestService userQuestService;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final UserQuestRepository userQuestRepository;
    private final AlarmService alarmService;
    private final AlarmRepository alarmRepository;
    private final RedisService redisService;

    @Override
    public Quest createQuest(QuestRequest.Create request) {
        Quest quest = Quest.build(request.getQuestType(), request.getName());
        return questRepository.save(quest);
    }

    @Override
    public Quest createWeekCountJobQuest(QuestRequest.JobQuest request) {
        Optional<Quest> existingQuest = questRepository.findByFormattedName(
                request.getDepartmentName(),
                request.getJobGroup(),
                request.getWeekCount()
        );
        if (existingQuest.isPresent()) {
            return existingQuest.get(); // 이미 존재하면 새로 생성하지 않고 반환
        }

        Optional<Department> optionalDepartment = departmentRepository.findDepartmentByName(request.getDepartmentName());
        if (optionalDepartment.isEmpty()) {
            throw new IllegalArgumentException("Department not found");
        }

        String questName = String.format(request.getDepartmentName() + " 직무그룹%d %d주차", request.getJobGroup(), request.getWeekCount());
        Quest quest = createQuest(new QuestRequest.Create("job", questName));
        LocalDateTime completedDateTime = START_DATE_TIME.plusWeeks(request.getWeekCount()-1);
        updateQuest(new QuestRequest.Complete(quest.getQuestId(), "", 0, false, completedDateTime));
        userQuestService.createJobUserQuest(request.getDepartmentName(), request.getJobGroup(), quest);
        return quest;
    }

    @Override
    public QuestResponse getWeekCountJobQuest(QuestRequest.JobQuest request) {
        Optional<Quest> optionalQuest = questRepository.findByFormattedName(request.getDepartmentName(), request.getJobGroup(), request.getWeekCount());
        if (optionalQuest.isEmpty()) {
            log.error("Quest not found");
            Quest quest = createWeekCountJobQuest(request);
            return QuestResponse.from(quest);
        }
        Quest quest = optionalQuest.get();
        return QuestResponse.from(quest);
    }

    @Override
    public QuestResponse updateWeekCountJobQuest(QuestRequest.UpdateJobQuest request) throws FirebaseMessagingException  {
        Optional<Quest> optionalQuest = questRepository.findByFormattedName(request.getDepartmentName(), request.getJobGroup(), request.getWeekCount());
        if (optionalQuest.isEmpty()) {
            throw new IllegalArgumentException("Quest not found");
        }
        Quest quest = optionalQuest.get();
        LocalDateTime completedDateTime = START_DATE_TIME.plusWeeks(request.getWeekCount()-1);
        quest.update(request.getGrade(), request.getExpAmount(), true, completedDateTime);
        updateExpAndAlarm(quest);
        return QuestResponse.from(quest);
    }

    @Override
    public QuestResponse getLeaderQuest(QuestRequest.LeaderQuest request) {
        Optional<User> optionalUser = userRepository.findUserByEmployeeNumAndName(request.getEmployeeNum(), request.getName());
        if (optionalUser.isEmpty()) {
            throw new UserApiException(UserErrorCode.USER_ID_NOT_FOUND);
        }

        User user = optionalUser.get();
        List<UserQuest> userQuests = userQuestRepository.findByUserWithQuest(user);
        String formattedQuestName = String.format("%d월 %s | %s", request.getMonth(), request.getQuestName(), request.getName());
        for (UserQuest userQuest : userQuests) {
            Quest quest = userQuest.getQuest();
            String questName = quest.getName();
            if (questName.equals(formattedQuestName)) {
                return QuestResponse.from(quest);
            }
        }

        Quest quest = createQuest(new QuestRequest.Create("leader", formattedQuestName));
        userQuestService.createUserQuest(new UserQuestRequest.Create(user.getUserId(), quest.getQuestId()));
        return QuestResponse.from(quest);
    }

    @Override
    public QuestResponse updateLeaderQuest(QuestRequest.UpdateLeaderQuest request) throws FirebaseMessagingException {
        String formattedQuestName = String.format("%d월 %s | %s", request.getMonth(), request.getQuestName(), request.getName());
        Optional<Quest> optionalQuest = questRepository.findQuestByName(formattedQuestName);
        if (optionalQuest.isEmpty()) {
            throw new IllegalArgumentException("Quest not found");
        }
        Quest quest = optionalQuest.get();
        LocalDate lastDayOfMonth = LocalDate.of(2025, request.getMonth(), 1)
                .withDayOfMonth(LocalDate.of(2025, request.getMonth(), 1).lengthOfMonth());
        LocalDateTime completedAt = LocalDateTime.of(lastDayOfMonth.getYear(), lastDayOfMonth.getMonth(), lastDayOfMonth.getDayOfMonth(), 23, 59);
        quest.update(request.getGrade(), request.getExpAmount(), true, completedAt);
        updateExpAndAlarm(quest);
        return QuestResponse.from(quest);
    }

    @Override
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
    @Transactional(readOnly = true)
    public List<Quest> getUserQuestList(User user) {
        List<UserQuest> userQuests = userQuestService.getUserQuests(user);
        List<Quest> quests = new ArrayList<>();
        for(UserQuest userQuest : userQuests) {
            quests.add(userQuest.getQuest());
        }
        return quests;
    }

    @Override
    public QuestResponse getCompanyQuest(QuestRequest.CompanyQuest request) {
        Optional<User> optionalUser = userRepository.findUserByEmployeeNumAndName(request.getEmployeeNum(), request.getName());
        if (optionalUser.isEmpty()) {
            throw new UserApiException(UserErrorCode.USER_ID_NOT_FOUND);
        }

        User user = optionalUser.get();
        List<UserQuest> userQuests = userQuestRepository.findByUserWithQuest(user);
        String format = String.format("%s | %s", request.getProjectName(), request.getName());
        for (UserQuest userQuest : userQuests) {
            Quest quest = userQuest.getQuest();
            String questName = quest.getName();
            if (questName.equals(format)) {
                return QuestResponse.from(quest);
            }
        }

        Quest quest = createQuest(new QuestRequest.Create("company", format));
        userQuestService.createUserQuest(new UserQuestRequest.Create(user.getUserId(), quest.getQuestId()));
        return QuestResponse.from(quest);
    }

    @Override
    public QuestResponse updateCompanyQuest(QuestRequest.UpdateCompanyQuest request) throws FirebaseMessagingException {
        Optional<Quest> optionalQuest = questRepository.findByQuestId(request.getQuestId());
        if (optionalQuest.isEmpty()) {
            throw new IllegalArgumentException("Quest not found");
        }
        
        Quest quest = optionalQuest.get();
        LocalDateTime completedAt = LocalDateTime.of(2025, request.getMonth(), request.getDay(), 0, 0);
        String questName = String.format("%s | %s", request.getProjectName(), request.getName());
        quest.updateCompanyProject(questName, request.getExpAmount(), true, completedAt);
        updateExpAndAlarm(quest);
        return QuestResponse.from(quest);
    }

    @Override
    public QuestResponse getHRQuest(QuestRequest.HRQuest request) {
        Optional<User> optionalUser = userRepository.findUserByEmployeeNumAndName(request.getEmployeeNum(), request.getName());
        if (optionalUser.isEmpty()) {
            throw new UserApiException(UserErrorCode.USER_ID_NOT_FOUND);
        }

        User user = optionalUser.get();
        List<UserQuest> userQuests = userQuestRepository.findByUserWithQuest(user);
        String season = request.getIsFirstHalf() ? "상반기" : "하반기";
        String format = String.format("%s 인사평가 | %s", season, request.getName());
        for (UserQuest userQuest : userQuests) {
            Quest quest = userQuest.getQuest();
            String questName = quest.getName();
            if (questName.equals(format)) {
                return QuestResponse.from(quest);
            }
        }

        Quest quest = createQuest(new QuestRequest.Create("hr", format));
        userQuestService.createUserQuest(new UserQuestRequest.Create(user.getUserId(), quest.getQuestId()));
        return QuestResponse.from(quest);
    }

    @Override
    public QuestResponse updateHRQuest(QuestRequest.UpdateHRQuest request) throws FirebaseMessagingException {
        Optional<Quest> optionalQuest = questRepository.findByQuestId(request.getQuestId());
        if (optionalQuest.isEmpty()) {
            throw new IllegalArgumentException("Quest not found");
        }

        Quest quest = optionalQuest.get();
        Integer month = request.getIsFirstHalf() ? 1 : 6;
        LocalDateTime completedAt = LocalDateTime.of(2025, month, 30, 0, 0);
        quest.update(request.getGrade(), request.getExpAmount(), true, completedAt);
        updateExpAndAlarm(quest);
        return QuestResponse.from(quest);
    }

    private void updateExpAndAlarm(Quest quest) throws FirebaseMessagingException {
        List<User> users = userQuestRepository.findUsersByQuestId(quest.getQuestId());
        boolean updateAlarm = false;
        int preExp = 0;

        for(User u : users) {
            List<Alarm> alarms = alarmRepository.findAllByQuestIdAndUser(quest.getQuestId(), u);
            if(alarms.size() > 0) {
                updateAlarm = true;
                preExp = alarms.get(0).getExp();
            }

            log.info("userID: " + u.getUserId() + ", questID: " + quest.getQuestId() + ", 알람 개수: "+ String.valueOf(alarms.size()));

            alarmService.createExpAlarm(u, quest, updateAlarm);
            redisService.updateDepartmentExp(u.getDepartment().getDepartmentId(), preExp, quest.getExpAmount());
        }
    }
}
