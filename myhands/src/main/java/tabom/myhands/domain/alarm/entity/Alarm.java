package tabom.myhands.domain.alarm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tabom.myhands.domain.board.entity.Board;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "alarm")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="alarm_id", nullable = false)
    private Long alarmId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean category;

    private Long boardId;

    private Long questId;

    private int exp;

    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "DATETIME")
    @CreatedDate
    private LocalDateTime createdAt;

    public static Alarm BoardAlarmCreate(User user, Board board){
        return Alarm.builder()
                .user(user)
                .title(board.getTitle())
                .category(true)
                .boardId(board.getBoardId())
                .build();
    }

    public static Alarm ExpAlarmCreate(User user, Quest quest) {
        String title = quest.getName().split("\\|")[0];

        return Alarm.builder()
                .user(user)
                .title(title)
                .category(false)
                .exp(quest.getExpAmount())
                .questId(quest.getQuestId())
                .build();
    }
}
