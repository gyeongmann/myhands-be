package tabom.myhands.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tabom.myhands.domain.board.dto.BoardRequest;
import tabom.myhands.domain.board.dto.BoardResponse;
import tabom.myhands.domain.board.entity.Board;
import tabom.myhands.domain.board.repository.BoardRepository;
import tabom.myhands.error.errorcode.BoardErrorCode;
import tabom.myhands.error.exception.BoardApiException;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;

    @Override
    public void create(Long userId, boolean isAdmin, BoardRequest.Create requestDto) {
        if(!isAdmin){
            throw new BoardApiException(BoardErrorCode.NOT_ADMIN);
        }
        if (requestDto.getTitle() == null || requestDto.getTitle().isEmpty() ||
                requestDto.getContent() == null || requestDto.getContent().isEmpty()) {
            throw new BoardApiException(BoardErrorCode.TITLE_OR_CONTENT_MISSING);
        }

        boardRepository.save(Board.build(requestDto, userId));
    }

    @Override
    public void edit(Long userId, boolean isAdmin, BoardRequest.Edit requestDto) {
        if(!isAdmin){
            throw new BoardApiException(BoardErrorCode.NOT_ADMIN);
        }
        if (requestDto.getTitle() == null || requestDto.getTitle().isEmpty() ||
                requestDto.getContent() == null || requestDto.getContent().isEmpty()) {
            throw new BoardApiException(BoardErrorCode.TITLE_OR_CONTENT_MISSING);
        }

        Board board = boardRepository.findByBoardId(requestDto.getBoardId())
                .orElseThrow(() -> new BoardApiException(BoardErrorCode.BOARD_ID_NOT_FOUND));

        board.edit(requestDto);
        boardRepository.save(board);
    }

    @Override
    public void delete(boolean isAdmin, Long boardId) {
        if(!isAdmin){
            throw new BoardApiException(BoardErrorCode.NOT_ADMIN);
        }
        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new BoardApiException(BoardErrorCode.BOARD_ID_NOT_FOUND));

        boardRepository.delete(board);
    }

    @Override
    public BoardResponse.Detail detail(Long boardId) {
        Board board = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new BoardApiException(BoardErrorCode.BOARD_ID_NOT_FOUND));
        return BoardResponse.Detail.build(board);
    }
}
