package tabom.myhands.domain.quest.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.servlet.http.HttpServletRequest;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    private final ExpService expService;

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
        LocalDateTime completedDateTime = START_DATE_TIME.plusWeeks(request.getWeekCount() - 1);
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
    public QuestResponse updateWeekCountJobQuest(QuestRequest.UpdateJobQuest request) {
        Optional<Quest> optionalQuest = questRepository.findByFormattedName(request.getDepartmentName(), request.getJobGroup(), request.getWeekCount());
        if (optionalQuest.isEmpty()) {
            throw new IllegalArgumentException("Quest not found");
        }
        Quest quest = optionalQuest.get();
        LocalDateTime completedDateTime = START_DATE_TIME.plusWeeks(request.getWeekCount() - 1);
        quest.update(request.getGrade(), request.getExpAmount(), true, completedDateTime);
        updateExpAndAlarm(quest);
        return QuestResponse.from(quest);
    }

    @Override
    public QuestResponse getLeaderQuest(QuestRequest.LeaderQuest request) {
        User user = getUserByEmployeeNumAndName(request.getEmployeeNum(), request.getName());
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
    public QuestResponse updateLeaderQuest(QuestRequest.UpdateLeaderQuest request) {
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
            throw new IllegalArgumentException("Quest not found");
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
        for (UserQuest userQuest : userQuests) {
            quests.add(userQuest.getQuest());
        }
        return quests;
    }

    @Override
    public QuestResponse getCompanyQuest(QuestRequest.CompanyQuest request) {
        User user = getUserByEmployeeNumAndName(request.getEmployeeNum(), request.getName());
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
    public QuestResponse updateCompanyQuest(QuestRequest.UpdateCompanyQuest request) {
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
        User user = getUserByEmployeeNumAndName(request.getEmployeeNum(), request.getName());
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

    private User getUserByEmployeeNumAndName(Integer employeeNum, String name) {
        Optional<User> optionalUser = userRepository.findUserByEmployeeNumAndName(employeeNum, name);
        if (optionalUser.isEmpty()) {
            throw new UserApiException(UserErrorCode.USER_ID_NOT_FOUND);
        }

        return optionalUser.get();
    }

    @Override
    public QuestResponse updateHRQuest(QuestRequest.UpdateHRQuest request) {
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


    private void updateExpAndAlarm(Quest quest) {
        List<User> users = userQuestRepository.findUsersByQuestId(quest.getQuestId());
        boolean updateAlarm = false;
        int preExp = 0;

        for (User u : users) {
            List<Alarm> alarms = alarmRepository.findAllByQuestIdAndUser(quest.getQuestId(), u);
            if (alarms.size() > 0) {
                updateAlarm = true;
                preExp = alarms.get(0).getExp();
            }

            log.info("userID: " + u.getUserId() + ", questID: " + quest.getQuestId() + ", 알람 개수: " + String.valueOf(alarms.size()));

            alarmService.createExpAlarm(u, quest, updateAlarm);
            redisService.updateDepartmentExp(u.getDepartment().getDepartmentId(), preExp, quest.getExpAmount());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QuestResponse.QuestCalendar getQuestCalendar(HttpServletRequest servletRequest, QuestRequest.QuestCalendar request) {
        Long userId = (Long) servletRequest.getAttribute("userId");
        User user = getUserByUserId(userId);
        List<Quest> questList = userQuestRepository.findQuestsByUserAndCompletedAtYearAndMonth(user, request.getYear(), request.getMonth());
        List<QuestResponse> list = new ArrayList<>();
        for (Quest quest : questList) {
            String name = quest.getName();
            if (name.endsWith("주차")) {
                name = name.substring(0, name.length() - 4);
            } else if (name.endsWith(user.getName())) {
                name = name.split(" " + "\\|")[0];
            }
            list.add(QuestResponse.from(quest, name));
        }

        return groupQuestsByWeekStartingSunday(list, request.getMonth());
    }

    @Override
    @Transactional(readOnly = true)
    public QuestResponse.QuestCalendar getQuestCalendarByParam(HttpServletRequest servletRequest, Integer year, Integer month) {
        Long userId = (Long) servletRequest.getAttribute("userId");
        User user = getUserByUserId(userId);
        List<Quest> questList = userQuestRepository.findQuestsByUserAndCompletedAtYearAndMonth(user, year, month);
        List<QuestResponse> list = new ArrayList<>();
        for (Quest quest : questList) {
            String name = quest.getName();
            if (name.endsWith("주차")) {
                name = name.substring(0, name.length() - 4);
            } else if (name.endsWith(user.getName())) {
                name = name.split(" \\|")[0];
            }
            list.add(QuestResponse.from(quest, name));
        }

        return groupQuestsByWeekStartingSunday(list, month);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestResponse.QuestStats getQuestStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = getUserByUserId(userId);
        List<String> challenges = getChallenges(user);
        Integer challengeCount = getChallengeCount(challenges);
        Integer questRate = getQuestRate(user);
        Integer maxCount = getMaxCount(user);
        Map<String, Integer> expHistory = getExpHistory(user);
        Integer historyCount = expHistory.size();
        return QuestResponse.QuestStats.from(challengeCount, challenges, questRate, maxCount, historyCount, expHistory);
    }

    private Integer getChallengeCount(List<String> challenges) {
        Integer challengeCount = 0;
        for (int i = 2; i >= 0; i--) {
            if (challenges.get(i).equals("MIN") || challenges.get(i).equals("FAIL")) {
                break;
            }
            challengeCount++;
        }
        return challengeCount + 1;
    }

    private Map<String, Integer> getExpHistory(User user) {
        int year = LocalDate.now().getYear();
        TreeMap<String, Integer> expHistory = new TreeMap<>();
        for (int i = 1; i <= 4; i++) {
            Integer yearExp = expService.getYearExp(user, year - i);
            expHistory.put(String.valueOf(year - i), yearExp);
        }
        return expHistory;
    }

    private Integer getMaxCount(User user) {
        // TODO: 임시 값 설정
        LocalDateTime startDate = LocalDateTime.of(2025, 2, 1, 0, 0, 0);
        LocalDateTime endDate = startDate.withHour(23).withMinute(59).withSecond(59);
        while (startDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
            startDate = startDate.minusDays(1);
        }

        LocalDate joinedAt = user.getJoinedAt();
        LocalDateTime joinedDateTime = LocalDateTime.of(joinedAt.getYear(), joinedAt.getMonth(), joinedAt.getDayOfMonth(), 0, 0, 0);
        int currentCount = 0;
        int maxCount = 0;
        while (joinedDateTime.isBefore(startDate)) {
            if (!userQuestRepository.findQuestsBetweenDates(user, startDate, endDate).isEmpty()) {
                currentCount++; // 완료된 퀘스트가 있으면 연속 증가
                maxCount = Math.max(maxCount, currentCount); // 최대값 갱신
            } else {
                currentCount = 0;
            }

            endDate = startDate;
            startDate = startDate.minusWeeks(1);
        }
        return maxCount;
    }

    private Integer getQuestRate(User user) {
        int totalQuestCount = userQuestRepository.findAllQuestsByYear(user, LocalDate.now().getYear()).size();
        int completedQuestCount = userQuestRepository.findCompletedQuestsByYear(user, LocalDate.now().getYear()).size();

        return Math.round((float) completedQuestCount / totalQuestCount * 100);
    }

    private List<String> getChallenges(User user) {
        LocalDateTime endDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        while (endDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            endDate = endDate.minusDays(1);
        }
        LocalDateTime startDate = endDate.minusWeeks(3);
        endDate = startDate.plusWeeks(1);

        List<String> challenges = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<Quest> questsBetweenDates = userQuestRepository.findQuestsBetweenDates(user, startDate, endDate);
            if (questsBetweenDates.isEmpty()) {
                challenges.add("FAIL");
                continue;
            }
            Quest quest = questsBetweenDates.get(0);
            if (quest.getQuestType().equals("hr") || quest.getQuestType().equals("company")) {
                challenges.add("ETC");
            } else {
                challenges.add(quest.getGrade());
            }
            startDate = startDate.plusWeeks(1);
            endDate = endDate.plusWeeks(1);
        }
        return challenges;
    }


    private User getUserByUserId(Long userId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            throw new UserApiException(UserErrorCode.USER_ID_NOT_FOUND);
        }
        return optionalUser.get();
    }

    private QuestResponse.QuestCalendar groupQuestsByWeekStartingSunday(List<QuestResponse> quests, Integer month) {
        // 정렬된 리스트를 반환하기 위한 Map
        List<QuestResponse>[] result;

        // 첫 번째 일요일을 기준으로 주차 구분
        LocalDate startDate = LocalDate.of(2025, month, 1); // 예시: 2025년 1월 1일을 시작일로 설정
        LocalDate firstSunday = getFirstSundayOfMonth(startDate); // 해당 월의 첫 번째 일요일 구하기

        // 첫 번째 일요일 이후 매 주 일요일 날짜 구하기
        List<LocalDate> sundayDates = getSundayDates(firstSunday);
        int weekCount = sundayDates.size();
        LocalDate lastSunday = sundayDates.get(sundayDates.size() - 1);
        if (lastSunday.plusDays(1).getMonthValue() == month) {
            weekCount++;
        }

        result = new List[weekCount];
        for (int i = 0; i < weekCount; i++) {
            result[i] = new ArrayList<>();
        }

        // 퀘스트 리스트를 날짜 순으로 정렬
        quests.sort(Comparator.comparing(QuestResponse::getCompletedAt));

        // Group quests by weeks
        for (QuestResponse quest : quests) {
            LocalDate questDate = quest.getCompletedAt().toLocalDate();
            LocalDate start = startDate;
            LocalDate end = firstSunday.plusDays(1);

            for (int i = 0; i < weekCount; i++) {
                if (!questDate.isBefore(start) && questDate.isBefore(end)) {
                    result[i].add(quest);
                    break;
                }

                start = end;
                end = end.plusWeeks(1);
            }
        }

        for (List<QuestResponse> list : result) {
            list.sort(Comparator.comparing(QuestResponse::getExpAmount).reversed());
        }

        return QuestResponse.QuestCalendar.from(weekCount, result);
    }

    private LocalDate getFirstSundayOfMonth(LocalDate date) {
        for (LocalDate today = date; today.isBefore(date.plusWeeks(1)); today = today.plusDays(1)) {
            if (today.getDayOfWeek() == DayOfWeek.SUNDAY) {
                return today;
            }
        }
        throw new IllegalStateException("No Sunday found in the given month!");
    }

    private List<LocalDate> getSundayDates(LocalDate firstSunday) {
        // 첫 번째 일요일을 시작으로, 매 주 일요일 날짜를 반환
        List<LocalDate> sundayDates = new ArrayList<>();
        LocalDate currentSunday = firstSunday;

        while (currentSunday.getMonthValue() == firstSunday.getMonthValue()) {
            sundayDates.add(currentSunday);
            currentSunday = currentSunday.plusWeeks(1);
        }

        return sundayDates;

    }
}
