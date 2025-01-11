package tabom.myhands.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserQuestRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create{
        private Long userId;
        private Long questId;
    }
}
