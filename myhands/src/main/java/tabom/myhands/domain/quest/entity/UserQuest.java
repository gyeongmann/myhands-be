package tabom.myhands.domain.quest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tabom.myhands.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    private UserQuest(User user, Quest quest) {
        this.user = user;
        this.quest = quest;
    }

    public static UserQuest build(User user, Quest quest) {
        return new UserQuest(user, quest);
    }

    @Override
    public String toString() {
        return "UserQuest{" +
                "userQuestId=" + userQuestId +
                ", user=" + user +
                ", quest=" + quest +
                '}';
    }
}
