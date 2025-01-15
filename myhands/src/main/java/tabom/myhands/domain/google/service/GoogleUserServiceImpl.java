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
import java.time.format.DateTimeParseException;
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
        Optional<User> opUser = userRepository.findByGoogleId(request.getGoogleId());
        Department department = departmentRepository.findDepartmentByName(request.getDepartment())
                .orElseThrow(() -> new UserApiException(UserErrorCode.INVALID_DEPARTMENT_VALUE));
        String joinedAt = request.getJoinedAt();
        LocalDate joinedAtDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        joinedAtDate = LocalDate.parse(joinedAt, formatter);

        if(opUser.isPresent()) {
            User user = opUser.get();
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

    public void createUserToSheet(User user) {

    }

    public void updatePasswordToSheet(User user) {

    }
}
