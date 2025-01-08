package tabom.myhands.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Join{
        private String name;
        private String id;
        private String password;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "Asia/Seoul" )
        private LocalDateTime joinedAt;
        private Integer departmentId;
        private Integer jobGroup; // 직무 그룹
        private String group; // 직군 (F,B,G,T)
    }
}
