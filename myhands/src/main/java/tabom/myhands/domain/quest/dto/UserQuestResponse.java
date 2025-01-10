package tabom.myhands.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tabom.myhands.domain.quest.entity.UserQuest;

@Getter
@AllArgsConstructor
public class UserQuestResponse {
    private Long userId;
    private Long questId;

    public static UserQuestResponse from(UserQuest userQuest) {
        return new UserQuestResponse(
                userQuest.getUser().getUserId(),
                userQuest.getQuest().getQuestId()
        );
    }
}
