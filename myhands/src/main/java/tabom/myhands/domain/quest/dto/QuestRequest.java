package tabom.myhands.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class QuestRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create{
        private String questType;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Complete {
        private Long questId;
        private String grade;
        private Integer expAmount;
        private Boolean isCompleted;
        private LocalDateTime completedAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestCalendar {
        private Integer year;
        private Integer month;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobQuest {
        private String departmentName;
        private Integer jobGroup;
        private Integer weekCount;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateJobQuest {
        private String departmentName;
        private Integer jobGroup;
        private Integer weekCount;
        private String grade;
        private Integer expAmount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaderQuest {
        private Integer month;
        private Integer employeeNum;
        private String name;
        private String questName;
        private String grade;
        private Integer expAmount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateLeaderQuest {
        private Integer month;
        private String name;
        private String questName;
        private String grade;
        private Integer expAmount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyQuest {
        private Integer month;
        private Integer day;
        private Integer employeeNum;
        private String name;
        private String projectName;
        private Integer expAmount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCompanyQuest {
        private Long questId;
        private Integer month;
        private Integer day;
        private Integer employeeNum;
        private String name;
        private String projectName;
        private Integer expAmount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HRQuest {
        private Boolean isFirstHalf;
        private Integer employeeNum;
        private String name;
        private String grade;
        private Integer expAmount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateHRQuest {
        private Long questId;
        private Boolean isFirstHalf;
        private String grade;
        private Integer expAmount;
    }
}
