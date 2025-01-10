package tabom.myhands.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class BoardRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create{
        private String title;
        private String content;
    }
}
