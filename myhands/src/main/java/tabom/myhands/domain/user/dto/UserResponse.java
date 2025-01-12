package tabom.myhands.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tabom.myhands.domain.user.entity.User;

import java.time.LocalDate;

public class UserResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login{
        private String accessToken;
        private String refreshToken;
        private boolean admin;

        public static UserResponse.Login build(String accessToken, String refreshToken, boolean admin) {
            return Login.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .admin(admin)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info{
        private Long userId;
        private String name;
        private String id;
        private String password;
        private Integer employeeNum;
        @JsonFormat(pattern = "yyyy-MM-dd" ,timezone = "Asia/Seoul")
        private LocalDate joinedAt;
        private String department;
        private Integer avartaId;
        private String level;

        public static UserResponse.Info build(User user) {
            return Info.builder()
                    .userId(user.getUserId())
                    .name(user.getName())
                    .id(user.getId())
                    .password(user.getPassword())
                    .employeeNum(user.getEmployeeNum())
                    .joinedAt(user.getJoinedAt())
                    .department(user.getDepartment().getName())
                    .avartaId(user.getAvatarId())
                    .level(user.getLevel())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserList{
        private Long userId;
        private String name;
        private Integer employeeNum;
        private String department;
        private Integer avartaId;
        private String level;

        public static UserResponse.UserList build(User user) {
            return UserList.builder()
                    .userId(user.getUserId())
                    .name(user.getName())
                    .employeeNum(user.getEmployeeNum())
                    .department(user.getDepartment().getName())
                    .avartaId(user.getAvatarId())
                    .level(user.getLevel())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail{
        private Long userId;
        private String name;
        private String id;
        private String password;
        private Integer employeeNum;
        @JsonFormat(pattern = "yyyy.MM.dd" ,timezone = "Asia/Seoul")
        private LocalDate joinedAt;
        private String department;
        private Integer jobGroup;

        public static UserResponse.Detail build(User user) {
            return Detail.builder()
                    .userId(user.getUserId())
                    .name(user.getName())
                    .id(user.getId())
                    .password(user.getPassword())
                    .employeeNum(user.getEmployeeNum())
                    .joinedAt(user.getJoinedAt())
                    .department(user.getDepartment().getName())
                    .jobGroup(user.getJobGroup())
                    .build();
        }
    }
}
