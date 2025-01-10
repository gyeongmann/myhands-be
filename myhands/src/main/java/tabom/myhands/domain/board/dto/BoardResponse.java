package tabom.myhands.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tabom.myhands.domain.board.entity.Board;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardList {
        private Long boardId;
        private String title;
        private String content;
        private String timeAgo; // 몇 시간 전인지 표시

        public static BoardResponse.BoardList build(Board board) {
            LocalDateTime now = LocalDateTime.now();
            String timeAgo = calculateTimeAgo(board.getCreatedAt(), now);
            return BoardResponse.BoardList.builder()
                    .boardId(board.getBoardId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .timeAgo(timeAgo)
                    .build();
        }

        private static String calculateTimeAgo(LocalDateTime createdAt, LocalDateTime now) {
            Duration duration = Duration.between(createdAt, now);

            if (duration.toMinutes() < 60) {
                return duration.toMinutes() + "분 전";
            } else if (duration.toHours() < 24) {
                return duration.toHours() + "시간 전";
            } else {
                return createdAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")); // 그 외는 날짜로 표시
            }
        }
    }
}
