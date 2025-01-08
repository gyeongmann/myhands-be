package tabom.myhands.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tabom.myhands.domain.user.dto.UserRequest;
import tabom.myhands.domain.user.entity.Department;
import tabom.myhands.domain.user.entity.User;
import tabom.myhands.domain.user.repository.DepartmentRepository;
import tabom.myhands.domain.user.repository.UserRepository;
import tabom.myhands.error.errorcode.UserErrorCode;
import tabom.myhands.error.exception.UserApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final LevelService levelService;

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

    private String generateEmployeeNum(LocalDateTime joinedAt) {
        String datePart = joinedAt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 해당 입사일의 최대 사번 뒤 두 자리 조회
        Integer maxSuffix = userRepository.findMaxSuffixByJoinedDate(datePart);
        int nextSuffix = (maxSuffix == null ? 1 : maxSuffix + 1);

        // 사번 생성 (YYYYMMDD + 순번)
        return datePart + String.format("%02d", nextSuffix);
    }
}
