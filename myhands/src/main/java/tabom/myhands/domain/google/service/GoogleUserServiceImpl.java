package tabom.myhands.domain.google.service;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleUserServiceImpl implements GoogleUserService {

    private final GoogleService googleService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public void createUser(UserRequest.GoogleJoin request) {
        List<User> users = userRepository.findByGoogleIdOrderByEmployeeNumDesc(request.getGoogleId());
        Department department = departmentRepository.findDepartmentByName(request.getDepartment())
                .orElseThrow(() -> new UserApiException(UserErrorCode.INVALID_DEPARTMENT_VALUE));
        String joinedAt = request.getJoinedAt();
        LocalDate joinedAtDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        joinedAtDate = LocalDate.parse(joinedAt, formatter);

        if(users.size() > 0) {
            User user = users.get(0);
            user.changeGoogleDetail(request, department, joinedAtDate);
            userRepository.save(user);
        } else {
            if(userRepository.existsByEmployeeNum(request.getEmployeeNum()) || userRepository.existsById(request.getId())) {
                throw new UserApiException(UserErrorCode.ID_ALREADY_EXISTS);
            }

            User user = User.googleUserbuild(request, department, joinedAtDate);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void createUserToSheet(User user) {
        Long googleId = userRepository.findMaxGoogleId() + 1;
        user.updateGoogleId(googleId);
        userRepository.save(user);

        List<List<Object>> values = new ArrayList<>();
        String range = "참고. 구성원 정보!B" + googleId + ":I" + googleId;

        List<Object> row = Arrays.asList(user.getEmployeeNum(), user.getName(), user.getJoinedAt().toString(), user.getDepartment().getName(), user.getJobGroup(), user.getLevel(), user.getId(), user.getPassword());
        values.add(row);
        googleService.writeToSheet(range, values);
    }

    @Override
    public void updateUserToSheet(User user) {
        if(user.getGoogleId() != null) {
            List<List<Object>> values = new ArrayList<>();
            String range = "참고. 구성원 정보!B" + user.getGoogleId() + ":F" + user.getGoogleId();

            List<Object> row = Arrays.asList(user.getEmployeeNum(), user.getName(), user.getJoinedAt().toString(), user.getDepartment().getName(), user.getJobGroup());
            values.add(row);
            googleService.writeToSheet(range, values);
        }
    }

    @Override
    public void updatePasswordToSheet(User user) {
        if(user.getGoogleId() != null) {
            List<List<Object>> values = new ArrayList<>();
            String range = "참고. 구성원 정보!J" + user.getGoogleId();

            List<Object> row = Arrays.asList(user.getPassword());
            values.add(row);
            googleService.writeToSheet(range, values);
        }
    }
}
