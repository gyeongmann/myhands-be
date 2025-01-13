package tabom.myhands.domain.quest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tabom.myhands.domain.user.entity.User;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_user_quest", columnNames = {"user_id", "quest_id"})
        }
)
public class UserQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userQuestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id")
    private Quest quest;

    public static UserQuest build(User user, Quest quest) {
        return UserQuest.builder()
                .user(user)
                .quest(quest)
                .build();
    }
}
