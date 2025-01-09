package tabom.myhands.domain.quest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@DiscriminatorColumn(name = "quest_type")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questId;

    private String name;

    private QuestGrade grade;

    private Integer expAmount;

    @ColumnDefault("false")
    private Boolean isCompleted;

    private LocalDateTime completedAt;

    private Quest(String name) {
        this.name = name;
    }

    public static Quest build(String name) {
        return new Quest(name);
    }

    public void update(QuestGrade grade, Integer expAmount, Boolean isCompleted, LocalDateTime completedAt) {
        this.grade = grade;
        this.expAmount = expAmount;
        this.isCompleted = isCompleted;
        this.completedAt = completedAt;
    }
}
