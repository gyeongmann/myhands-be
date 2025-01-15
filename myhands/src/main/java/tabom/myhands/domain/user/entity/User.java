package tabom.myhands.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tabom.myhands.domain.user.dto.UserRequest;
import tabom.myhands.error.errorcode.UserErrorCode;
import tabom.myhands.error.exception.UserApiException;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(nullable = false, unique = true)
    private String id;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "employee_num", unique = true, nullable = false)
    private Integer employeeNum;

    @Column(nullable = false)
    private String name;

    @Column(name = "joined_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd" , timezone = "Asia/Seoul" )
    private LocalDate joinedAt;

    @Column(name = "avatar_id")
    private Integer avatarId;

    private Integer exp;

    @Column(name = "job_group", nullable = false)
    private Integer jobGroup;

    @Column(nullable = false)
    private String level;

    @Column(name = "device_token")
    private String deviceToken;

    private Long googleId;

    public static User build(UserRequest.Join request, Department department, String employeeNum, String level) {

        return User.builder()
                .department(department)
                .name(request.getName())
                .id(request.getId())
                .password(request.getPassword())
                .employeeNum(Integer.valueOf(employeeNum))
                .joinedAt(request.getJoinedAt())
                .avatarId(1)
                .exp(0)
                .jobGroup(request.getJobGroup())
                .level(level)
                .build();
    }

    public static User googleUserbuild(UserRequest.GoogleJoin request, Department department, LocalDate joinedAt) {

        return User.builder()
                .department(department)
                .name(request.getName())
                .id(request.getId())
                .password(request.getPassword())
                .employeeNum(request.getEmployeeNum())
                .joinedAt(joinedAt)
                .avatarId(1)
                .exp(0)
                .jobGroup(request.getJobGroup())
                .level(request.getLevel())
                .googleId(request.getGoogleId())
                .build();
    }

    public void changeGoogleDetail(UserRequest.GoogleJoin request, Department department, LocalDate joinedAt) {
        this.employeeNum = request.getEmployeeNum();
        this.department = department;
        this.jobGroup = request.getJobGroup();
        this.name = request.getName();
        this.joinedAt = joinedAt;
    }

    public void changeDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new UserApiException(UserErrorCode.PASSWORD_CANNOT_BE_EMPTY);
        }
        this.password = newPassword;
    }

    public void changeImage(Integer avatarId) {
        if (avatarId == null) {
            throw new UserApiException(UserErrorCode.AVARTAID_CANNOT_BE_EMPTY);
        }
        this.avatarId = avatarId;
    }

    public void changeDetail(UserRequest.Update request, Department department) {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new UserApiException(UserErrorCode.NAME_CANNOT_BE_EMPTY);
        }
        if (request.getEmployeeNum() == null) {
            throw new UserApiException(UserErrorCode.EMPLOYEE_NUM_CANNOT_BE_EMPTY);
        }
        if (department == null) {
            throw new UserApiException(UserErrorCode.DEPARTMENT_CANNOT_BE_EMPTY);
        }
        if (request.getJobGroup() == null) {
            throw new UserApiException(UserErrorCode.JOB_GROUP_CANNOT_BE_EMPTY);
        }
        if (request.getJoinedAt() == null) {
            throw new UserApiException(UserErrorCode.JOINED_AT_CANNOT_BE_EMPTY);
        }

        this.name = request.getName();
        this.employeeNum = request.getEmployeeNum();
        this.department = department;
        this.jobGroup = request.getJobGroup();
        this.joinedAt = request.getJoinedAt();
    }

    public void changeGoogleId(Long googleId) {
        this.googleId = googleId;
    }

}
