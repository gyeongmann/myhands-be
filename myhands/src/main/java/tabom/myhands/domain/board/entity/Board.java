package tabom.myhands.domain.board.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tabom.myhands.domain.board.dto.BoardRequest;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "content", length = 1000, nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "google_id")
    private Long googleId;

    public static Board build(BoardRequest.Create request, Long userId) {
        return Board.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Board googleBoardBuild(BoardRequest.Edit request, Long userId) {
        return Board.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .googleId(request.getBoardId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateGoogleId(Long googleId) {
        this.googleId = googleId;
    }

    public void edit(BoardRequest.Edit request) {
        this.title = request.getTitle();
        this.content = request.getContent();
    }
}
