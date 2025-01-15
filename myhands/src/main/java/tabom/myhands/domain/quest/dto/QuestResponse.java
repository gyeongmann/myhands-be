package tabom.myhands.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tabom.myhands.domain.quest.entity.Quest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    public static QuestResponse from(Quest quest, String questName) {
        return new QuestResponse(
                quest.getQuestId(),
                quest.getQuestType(),
                questName,
                quest.getGrade(),
                quest.getExpAmount(),
                quest.getIsCompleted(),
                quest.getCompletedAt()
        );
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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestStats {
        private Integer challengeCount;
        private List<String> resultList;
        private Integer questRate;
        private Integer maxCount;
        private Integer historySize;
        private Map<String, Integer> expHistory;

        public static QuestStats from(Integer challengeCount, List<String> resultList, Integer questRate, Integer maxCount, Integer historySize, Map<String, Integer> expHistory) {
            return new QuestStats(challengeCount, resultList, questRate, maxCount, historySize, expHistory);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestResponseTimeFormat {
        private Long questId;
        private String questType;
        private String name;
        private String grade;
        private Integer expAmount;
        private Boolean isCompleted;
        private String completedAt;

        public static QuestResponseTimeFormat from(Quest quest, String name, String grade, String completedAt) {
            return new QuestResponseTimeFormat(quest.getQuestId(), quest.getQuestType(), name, grade, quest.getExpAmount(), quest.getIsCompleted(), completedAt);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestPage {
        private List<QuestResponseTimeFormat> quests;
        private Boolean hasMore;
        private Integer totalPages;
        private Long totalElements;
        private Integer currentPage;

        public static QuestPage from(List<QuestResponseTimeFormat> quests, Boolean hasMore, Integer totalPages, Long totalElements, Integer currentPage) {
            return new QuestPage(quests, hasMore, totalPages, totalElements, currentPage);
        }
    }
}
