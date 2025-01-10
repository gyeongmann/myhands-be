package tabom.myhands.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tabom.myhands.domain.quest.entity.Quest;

import java.time.LocalDateTime;

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
}
