package tabom.myhands.domain.board.service;

import tabom.myhands.domain.board.dto.BoardRequest;
import tabom.myhands.domain.board.dto.BoardResponse;

import java.util.List;

public interface BoardService {
    void create(Long userId, boolean isAdmin, BoardRequest.Create requestDto);
    void edit(Long userId, boolean isAdmin, BoardRequest.Edit requestDto);
    void delete(boolean isAdmin, Long boardId);
    BoardResponse.Detail detail(Long boardId);
    List<BoardResponse.BoardList> overview(int size);
    List<BoardResponse.BoardList> list(int size, Long lastId);
    List<BoardResponse.BoardList> search(String word, int size, Long lastId);
}
