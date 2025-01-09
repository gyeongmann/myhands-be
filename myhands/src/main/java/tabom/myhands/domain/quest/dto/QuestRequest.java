package tabom.myhands.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class QuestRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompleteQuest {
        private Long QuestId;
        private String grade;
        private Integer expAmount;
        private Boolean isCompleted;
        private LocalDateTime completedAt;
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
