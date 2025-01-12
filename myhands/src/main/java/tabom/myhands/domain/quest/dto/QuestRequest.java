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
    public static class QuestCalendar {
        private Long userId;
        private Integer year;
        private Integer month;
    }
}
