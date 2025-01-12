package tabom.myhands.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class UserRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Join{
        private String name;
        private String id;
        private String password;
        @JsonFormat(pattern = "yyyy-MM-dd" , timezone = "Asia/Seoul" )
        private LocalDate joinedAt;
        private Integer departmentId;
        private Integer jobGroup; // 직무 그룹
        private String group; // 직군 (F,B,G,T)
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login{
        private String id;
        private String password;
        private String deviceToken;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Password{
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        private Long userId;
        private String name;
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate joinedAt;
        private Integer departmentId;
        private Integer jobGroup;
        private Integer employeeNum;
    }
}
