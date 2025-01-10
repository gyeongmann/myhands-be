package tabom.myhands.domain.quest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questId;

    private String questType;

    private String name;

    private String grade;

    private Integer expAmount;

    private Boolean isCompleted;

    private LocalDateTime completedAt;

    private Quest(String questType, String name) {
        this.questType = questType;
        this.name = name;
        this.isCompleted = false;
    }

    public static Quest build(String questType, String name) {
        return new Quest(questType, name);
    }

    public void update(String grade, Integer expAmount, Boolean isCompleted, LocalDateTime completedAt) {
        this.grade = grade;
        this.expAmount = expAmount;
        this.isCompleted = isCompleted;
        this.completedAt = completedAt;
    }

    @Override
    public String toString() {
        return "Quest{" +
                "questId=" + questId +
                ", questType='" + questType + '\'' +
                ", name='" + name + '\'' +
                ", grade='" + grade + '\'' +
                ", expAmount=" + expAmount +
                ", isCompleted=" + isCompleted +
                ", completedAt=" + completedAt +
                '}';
    }
}
