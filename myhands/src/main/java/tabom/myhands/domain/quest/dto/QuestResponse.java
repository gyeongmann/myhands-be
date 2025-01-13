package tabom.myhands.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tabom.myhands.domain.quest.entity.Quest;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class QuestResponse {
    private Long questId;
    private String questType;
    private String name;
    private String grade;
    private Integer expAmount;
    private Boolean isCompleted;
    private LocalDateTime completedAt;

    public static QuestResponse from(Quest quest) {
        return new QuestResponse(
                quest.getQuestId(),
                quest.getQuestType(),
                quest.getName(),
                quest.getGrade(),
                quest.getExpAmount(),
                quest.getIsCompleted(),
                quest.getCompletedAt()
        );
    }

    @Override
    public String toString() {
        return "QuestResponse{" +
                "questId=" + questId +
                ", questType='" + questType + '\'' +
                ", name='" + name + '\'' +
                ", grade='" + grade + '\'' +
                ", expAmount=" + expAmount +
                ", isCompleted=" + isCompleted +
                ", completedAt=" + completedAt +
                '}';
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestCalendar {
        private Integer weekCount;
        private List<QuestResponse>[] questList;

        public static QuestCalendar from(Integer weekCount, List<QuestResponse>[] questList) {
            return new QuestCalendar(weekCount, questList);
        }
    }
}
