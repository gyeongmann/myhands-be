package tabom.myhands.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tabom.myhands.domain.quest.dto.QuestResponse;
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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageResponse {
        private Fortune fortune;
        private LevelRate levelRate;
        private RecentExp recentExp;
        private ThisYearExp thisYearExp;
        private LastYearExp lastYearExp;

        public static MyPageResponse build(Fortune fortune, LevelRate levelRate, RecentExp recentExp, ThisYearExp thisYearExp, LastYearExp lastYearExp) {
            return new MyPageResponse(fortune, levelRate, recentExp, thisYearExp, lastYearExp);
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Fortune {
            private LocalDate date;
            private String contents;

            public static Fortune build(LocalDate date, String contents) {
                return new Fortune(date, contents);
            }
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LevelRate {
            private String currentLevel;
            private int currentExp;
            private String nextLevel;
            private int leftExp;
            private int percent;

            public static LevelRate build(String currentLevel, int currentExp, String nextLevel, int leftExp, int percent) {
                return new LevelRate(currentLevel, currentExp, nextLevel, leftExp, percent);
            }
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RecentExp {
            private Long questId;
            private String questType; // job, leader, company
            private String name;
            private String grade;
            private Integer expAmount;
            private Boolean isCompleted;
            private String completedAt;

            public static RecentExp build(QuestResponse quest, String name, String grade, String completedAt) {
                return new RecentExp(quest.getQuestId(), quest.getQuestType(), name, grade, quest.getExpAmount(), quest.getIsCompleted(), completedAt);
            }
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ThisYearExp {
            private int expAmount;
            private int percent;

            public static ThisYearExp build(Integer expAmount, Integer percent) {
                return new ThisYearExp(expAmount, percent);
            }
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LastYearExp {
            private int expAmount;
            private int percent;

            public static LastYearExp build(Integer expAmount, Integer percent) {
                return new LastYearExp(expAmount, percent);
            }
        }
    }
}
