package tabom.myhands.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create{
        private String title;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Edit{
        private Long boardId;
        private String title;
        private String content;
    }
}
