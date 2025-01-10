package tabom.myhands.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tabom.myhands.domain.board.entity.Board;

import java.time.LocalDateTime;

public class BoardResponse {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail{
        private String title;
        private String content;
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm" ,timezone = "Asia/Seoul")
        private LocalDateTime createdAt;

        public static BoardResponse.Detail build(Board board) {
            return BoardResponse.Detail.builder()
                    .title(board.getTitle())
                    .content(board.getContent())
                    .createdAt(board.getCreatedAt())
                    .build();
        }
    }
}
