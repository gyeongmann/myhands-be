package tabom.myhands.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tabom.myhands.common.infra.redis.RedisService;
import tabom.myhands.common.jwt.JwtTokenProvider;
import tabom.myhands.domain.user.dto.UserRequest;
import tabom.myhands.domain.user.dto.UserResponse;
import tabom.myhands.domain.user.entity.Admin;
import tabom.myhands.domain.user.entity.Department;
import tabom.myhands.domain.user.entity.User;
import tabom.myhands.domain.user.repository.AdminRepository;
import tabom.myhands.domain.user.repository.DepartmentRepository;
import tabom.myhands.domain.user.repository.UserRepository;
import tabom.myhands.error.errorcode.UserErrorCode;
import tabom.myhands.error.exception.UserApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final DepartmentRepository departmentRepository;
    private final LevelService levelService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Transactional
    @Override
    public void join(UserRequest.Join request) {

        if(userRepository.findById(request.getId()).isPresent()){
            throw new UserApiException(UserErrorCode.ID_ALREADY_EXISTS);
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new UserApiException(UserErrorCode.INVALID_DEPARTMENT_VALUE));

        String employeeNum = generateEmployeeNum(request.getJoinedAt());

        String level = levelService.getLowestLevel(request.getGroup());

        User user = User.build(request, department, employeeNum, level);
        userRepository.save(user);
    }

    @Override
    public void isDuplicate(String id) {
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

    private String generateEmployeeNum(LocalDateTime joinedAt) {
        String datePart = joinedAt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 해당 입사일의 최대 사번 뒤 두 자리 조회
        Integer maxSuffix = userRepository.findMaxSuffixByJoinedDate(datePart);
        int nextSuffix = (maxSuffix == null ? 1 : maxSuffix + 1);

        // 사번 생성 (YYYYMMDD + 순번)
        return datePart + String.format("%02d", nextSuffix);
    }
}
