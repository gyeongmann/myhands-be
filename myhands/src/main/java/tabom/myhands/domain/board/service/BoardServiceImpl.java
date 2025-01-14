package tabom.myhands.domain.board.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tabom.myhands.domain.alarm.service.AlarmService;
import tabom.myhands.domain.board.dto.BoardRequest;
import tabom.myhands.domain.board.dto.BoardResponse;
import tabom.myhands.domain.board.entity.Board;
import tabom.myhands.domain.board.repository.BoardRepository;
import tabom.myhands.domain.google.service.GoogleBoardService;
import tabom.myhands.error.errorcode.BoardErrorCode;
import tabom.myhands.error.exception.BoardApiException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;
    private final AlarmService alarmService;
    private final GoogleBoardService googleBoardService;

    @Override
    @Transactional
    public void create(Long userId, boolean isAdmin, BoardRequest.Create requestDto) throws FirebaseMessagingException {
        if(!isAdmin){
            throw new BoardApiException(BoardErrorCode.NOT_ADMIN);
        }
        if (requestDto.getTitle() == null || requestDto.getTitle().isEmpty() ||
                requestDto.getContent() == null || requestDto.getContent().isEmpty()) {
            throw new BoardApiException(BoardErrorCode.TITLE_OR_CONTENT_MISSING);
        }

        Board board = Board.build(requestDto, userId);
        boardRepository.save(board);
        googleBoardService.createBoardToSheet(board);
        alarmService.createBoardAlarm(board);
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

    @Override
    public List<BoardResponse.BoardList> overview(int size) {
        if(size <= 0){
            throw new BoardApiException(BoardErrorCode.INVALID_SIZE_PARAMETER);
        }

        List<Board> boards = boardRepository.findFirstPage(size);

        return boards.stream()
                .map(BoardResponse.BoardList::build)
                .toList();
    }

    @Override
    public List<BoardResponse.BoardList> list(int size, Long lastId) {
        if(size <= 0){
            throw new BoardApiException(BoardErrorCode.INVALID_SIZE_PARAMETER);
        }
        if(lastId != null) {
            boardRepository.findByBoardId(lastId).orElseThrow(() -> new BoardApiException(BoardErrorCode.BOARD_ID_NOT_FOUND));
        }

        List<Board> boards = (lastId == null)
                ? boardRepository.findAllFirstPage(size)
                : boardRepository.findAllLastId(lastId, size);

        return boards.stream()
                .map(BoardResponse.BoardList::build)
                .toList();
    }

    @Override
    public List<BoardResponse.BoardList> search(String word, int size, Long lastId) {
        if(size <= 0){
            throw new BoardApiException(BoardErrorCode.INVALID_SIZE_PARAMETER);
        }
        if(lastId != null) {
            boardRepository.findByBoardId(lastId).orElseThrow(() -> new BoardApiException(BoardErrorCode.BOARD_ID_NOT_FOUND));
        }
        List<Board> boards = (lastId == null)
                ? boardRepository.findWordFirstPage(word, size)
                : boardRepository.findWordLastId(word, lastId, size);

        return boards.stream()
                .map(BoardResponse.BoardList::build)
                .toList();
    }
}
