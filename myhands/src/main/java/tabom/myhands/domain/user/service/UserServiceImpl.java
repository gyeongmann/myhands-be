package tabom.myhands.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tabom.myhands.common.infra.redis.RedisService;
import tabom.myhands.common.jwt.JwtTokenProvider;
import tabom.myhands.domain.fortune.service.FortuneService;
import tabom.myhands.domain.google.service.GoogleUserService;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.quest.entity.UserQuest;
import tabom.myhands.domain.quest.repository.UserQuestRepository;
import tabom.myhands.domain.quest.service.ExpService;
import tabom.myhands.domain.user.dto.UserRequest;
import tabom.myhands.domain.user.dto.UserResponse;
import tabom.myhands.domain.user.entity.Admin;
import tabom.myhands.domain.user.entity.Department;
import tabom.myhands.domain.user.entity.User;
import tabom.myhands.domain.user.repository.AdminRepository;
import tabom.myhands.domain.user.repository.DepartmentRepository;
import tabom.myhands.domain.user.repository.UserRepository;
import tabom.myhands.error.errorcode.BoardErrorCode;
import tabom.myhands.error.errorcode.UserErrorCode;
import tabom.myhands.error.exception.BoardApiException;
import tabom.myhands.error.exception.UserApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final DepartmentRepository departmentRepository;
    private final LevelService levelService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final FortuneService fortuneService;
    private final UserQuestRepository userQuestRepository;
    private final ExpService expService;
    private final GoogleUserService googleUserService;

    @Transactional
    @Override
    public void join(boolean isAdmin, UserRequest.Join request) {
        if (!isAdmin) {
            throw new BoardApiException(BoardErrorCode.NOT_ADMIN);
        }
        if (userRepository.findById(request.getId()).isPresent()) {
            throw new UserApiException(UserErrorCode.ID_ALREADY_EXISTS);
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new UserApiException(UserErrorCode.INVALID_DEPARTMENT_VALUE));

        String employeeNum = generateEmployeeNum(request.getJoinedAt().atStartOfDay());

        String level = levelService.getLowestLevel(request.getGroup());

        User user = User.build(request, department, employeeNum, level);
        userRepository.save(user);
        googleUserService.createUserToSheet(user);
    }

    @Override
    public void isDuplicate(boolean isAdmin, String id) {
        if (!isAdmin) {
            throw new BoardApiException(BoardErrorCode.NOT_ADMIN);
        }
        if (userRepository.existsById(id)) {
            throw new UserApiException(UserErrorCode.ID_ALREADY_EXISTS);
        }
    }

    @Override
    public UserResponse.Login login(UserRequest.Login request) {
        Optional<User> userOptional = userRepository.findById(request.getId());
        Optional<Admin> adminOptional = adminRepository.findById(request.getId());

        if (userOptional.isEmpty() && adminOptional.isEmpty()) { // 없는 아이디로 로그인 시도
            throw new UserApiException(UserErrorCode.LOGIN_FAILED);
        }

        boolean isAdmin = adminOptional.isPresent();

        if (isAdmin) { // Admin인 경우
            Admin admin = adminOptional.get();
            if (!admin.getPassword().equals(request.getPassword())) {
                throw new UserApiException(UserErrorCode.LOGIN_FAILED);
            }
        } else { // User인 경우
            User user = userOptional.get();
            if (!user.getPassword().equals(request.getPassword())) {
                throw new UserApiException(UserErrorCode.LOGIN_FAILED);
            }
            user.changeDeviceToken(request.getDeviceToken());
            userRepository.save(user);
        }

        Long userId = isAdmin ? adminOptional.get().getAdminId() : userOptional.get().getUserId();
        String accessToken = jwtTokenProvider.generateAccessToken(userId, isAdmin);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId, isAdmin);

        return UserResponse.Login.build(accessToken, refreshToken, isAdmin);
    }

    @Override
    public void logout(Long userId, boolean isAdmin, String accessToken) {
        redisService.deleteRefreshToken(userId, isAdmin);
        long expirationTime = jwtTokenProvider.getExpirationTime(accessToken);
        redisService.addToBlacklist(accessToken, expirationTime);
    }

    @Override
    public void editPassword(Long userId, UserRequest.Password requestDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserApiException(UserErrorCode.USER_ID_NOT_FOUND));
        user.changePassword(requestDto.getPassword());
        userRepository.save(user);
    }

    @Override
    public void editImage(Long userId, Integer avartaId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserApiException(UserErrorCode.USER_ID_NOT_FOUND));
        user.changeImage(avartaId);
        userRepository.save(user);
    }

    @Override
    public UserResponse.Info getInfo(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserApiException(UserErrorCode.USER_ID_NOT_FOUND));

        return UserResponse.Info.build(user);
    }

    @Override
    public List<UserResponse.UserList> getList(boolean isAdmin) {
        if (!isAdmin) {
            throw new BoardApiException(BoardErrorCode.NOT_ADMIN);
        }
        List<User> users = userRepository.findAllUser();
        return users.stream()
                .map(UserResponse.UserList::build)
                .toList();
    }

    @Override
    public UserResponse.Detail getDetail(boolean isAdmin, Long userId) {
        if (!isAdmin) {
            throw new BoardApiException(BoardErrorCode.NOT_ADMIN);
        }
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserApiException(UserErrorCode.USER_ID_NOT_FOUND));
        return UserResponse.Detail.build(user);
    }

    @Override
    public void update(boolean isAdmin, UserRequest.Update requestDto) {
        if (!isAdmin) {
            throw new BoardApiException(BoardErrorCode.NOT_ADMIN);
        }

        User user = userRepository.findByUserId(requestDto.getUserId())
                .orElseThrow(() -> new UserApiException(UserErrorCode.USER_ID_NOT_FOUND));

        Department department = departmentRepository.findById(requestDto.getDepartmentId())
                .orElseThrow(() -> new UserApiException(UserErrorCode.INVALID_DEPARTMENT_VALUE));

        user.changeDetail(requestDto, department);
        userRepository.save(user);
    }

    @Override
    public void isDuplicateNum(boolean isAdmin, Integer num) {
        if (!isAdmin) {
            throw new BoardApiException(BoardErrorCode.NOT_ADMIN);
        }

        if (userRepository.existsByEmployeeNum(num)) {
            throw new UserApiException(UserErrorCode.EMPLOYEE_NUM_ALREADY_EXISTS);
        }
    }

    private String generateEmployeeNum(LocalDateTime joinedAt) {
        String datePart = joinedAt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 해당 입사일의 최대 사번 뒤 두 자리 조회
        Integer maxSuffix = userRepository.findMaxSuffixByJoinedDate(datePart);
        int nextSuffix = (maxSuffix == null ? 1 : maxSuffix + 1);

        // 사번 생성 (YYYYMMDD + 순번)
        return datePart + String.format("%02d", nextSuffix);
    }

    @Override
    public User getUserByUserId(Long userId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            throw new UserApiException(UserErrorCode.USER_ID_NOT_FOUND);
        }
        return optionalUser.get();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse.MyPageResponse getMyPageInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = getUserByUserId(userId);
        Integer currentExp = expService.getCurrentExp(user);
        String group = user.getLevel().substring(0, 1);
        String currentLevel = levelService.calculateLevel(group, currentExp);
        Map.Entry<String, Integer> nextLevelMap = levelService.getNextLevel(group, currentLevel);

        // fortune
        UserResponse.MyPageResponse.Fortune fortune = getFortune(String.valueOf(userId));

        // levelRate
        UserResponse.MyPageResponse.LevelRate levelRate = getLevelRate(currentExp, currentLevel, nextLevelMap);

        // recentExp
        UserResponse.MyPageResponse.RecentExp recentExp = getRecentExp(user);

        // thisYearExp
        UserResponse.MyPageResponse.ThisYearExp thisYearExp = getThisYearExp(user);

        // lastYearExp
        UserResponse.MyPageResponse.LastYearExp lastYearExp = getLastYearExp(user);

        return UserResponse.MyPageResponse.build(fortune, levelRate, recentExp, thisYearExp, lastYearExp);
    }

    private UserResponse.MyPageResponse.Fortune getFortune(String userId) {
        String fortune = fortuneService.getFortune(Integer.parseInt(userId));
        return UserResponse.MyPageResponse.Fortune.build(LocalDate.now(), fortune);
    }

    private UserResponse.MyPageResponse.LevelRate getLevelRate(Integer currentExp, String currentLevel, Map.Entry<String, Integer> nextLevelMap) {
        String nextLevel = nextLevelMap.getKey();
        Integer nextExp = nextLevelMap.getValue();
        int leftExp = nextExp - currentExp;
        int percent = Math.round((float) currentExp / nextExp * 100);

        return UserResponse.MyPageResponse.LevelRate.build(currentLevel, currentExp, nextLevel, leftExp, percent);
    }

    private UserResponse.MyPageResponse.RecentExp getRecentExp(User user) {
        List<UserQuest> recentExpList = userQuestRepository.findMostRecentCompletedQuest(user, PageRequest.of(0, 1));
        if (recentExpList.isEmpty()) {
            return new UserResponse.MyPageResponse.RecentExp();
        }
        UserQuest userQuest = recentExpList.get(0);
        Quest quest = userQuest.getQuest();
        return UserResponse.MyPageResponse.RecentExp.build(quest);
    }

    private UserResponse.MyPageResponse.ThisYearExp getThisYearExp(User user) {
        Integer expAmount = expService.getThisYearExp(user);
        Integer percent = Math.round((float) expAmount / 9000 * 100);
        return UserResponse.MyPageResponse.ThisYearExp.build(expAmount, percent);
    }

    private UserResponse.MyPageResponse.LastYearExp getLastYearExp(User user) {
        Integer expAmount = expService.getLastYearExp(user);
        String group = user.getLevel().substring(0, 1);
        Map.Entry<String, Integer> nextLevelMap = levelService.getNextLevel(group, user.getLevel());
        Integer nextLevelExp = nextLevelMap.getValue();
        Integer percent = Math.round((float) expAmount / nextLevelExp * 100);
        return UserResponse.MyPageResponse.LastYearExp.build(expAmount, percent);
    }
}
