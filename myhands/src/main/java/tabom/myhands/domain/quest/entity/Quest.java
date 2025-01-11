package tabom.myhands.domain.quest.entity;

import jakarta.persistence.*;
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

    @Column(unique = true)
    private String name;

    private String grade;

    private Integer expAmount;

    private Boolean isCompleted;

    private LocalDateTime completedAt;

    public static Quest build(String questType, String name) {
        return Quest.builder()
                .questType(questType)
                .name(name)
                .isCompleted(false) // 기본값 설정
                .build();
    }

    public void update(String grade, Integer expAmount, Boolean isCompleted, LocalDateTime completedAt) {
        this.grade = grade;
        this.expAmount = expAmount;
        this.isCompleted = isCompleted;
        this.completedAt = completedAt;
    }
}
