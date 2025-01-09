package tabom.myhands.domain.quest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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

    @ColumnDefault("false")
    private Boolean isCompleted;

    private LocalDateTime completedAt;

    private Quest(String questType, String name) {
        this.questType = questType;
        this.name = name;
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
}
