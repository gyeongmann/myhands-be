package tabom.myhands.domain.board.service;

import tabom.myhands.domain.board.dto.BoardRequest;
import tabom.myhands.domain.board.dto.BoardResponse;

public interface BoardService {
    void create(Long userId, boolean isAdmin, BoardRequest.Create requestDto);
    void edit(Long userId, boolean isAdmin, BoardRequest.Edit requestDto);
    void delete(boolean isAdmin, Long boardId);
    BoardResponse.Detail detail(Long boardId);
}
