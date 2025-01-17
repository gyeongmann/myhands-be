package tabom.myhands.domain.google.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tabom.myhands.domain.alarm.service.AlarmService;
import tabom.myhands.domain.board.dto.BoardRequest;
import tabom.myhands.domain.board.entity.Board;
import tabom.myhands.domain.board.repository.BoardRepository;
import tabom.myhands.error.errorcode.BoardErrorCode;
import tabom.myhands.error.exception.BoardApiException;

import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleBoardServiceImpl implements GoogleBoardService {
    private final BoardRepository boardRepository;
    private final AlarmService alarmService;
    private final GoogleService googleService;

    @Override
    @Transactional
    public void create(Long userId, BoardRequest.Edit requestDto) {
        if (requestDto.getTitle() == null || requestDto.getTitle().isEmpty() ||
                requestDto.getContent() == null || requestDto.getContent().isEmpty()) {
            throw new BoardApiException(BoardErrorCode.TITLE_OR_CONTENT_MISSING);
        }

        List<Board> boards = boardRepository.findByGoogleIdOrderByCreatedAtDesc(requestDto.getBoardId());
        if(boards.size() > 0) {
            Board board = boards.get(0);
            board.edit(requestDto);
            boardRepository.save(board);
        } else {
            Board board = Board.googleBoardBuild(requestDto, userId);
            boardRepository.save(board);
            alarmService.createBoardAlarm(board);
        }
    }

    public void createBoardToSheet(Board board) {
        Long googleId = boardRepository.findMaxGoogleId() + 1;
        board.updateGoogleId(googleId);
        boardRepository.save(board);

        List<List<Object>> values = new ArrayList<>();
        String range = "게시판!B" + (googleId + 6) + ":D" + (googleId + 6);  // 데이터를 삽입할 범위
        List<Object> row = Arrays.asList(googleId, board.getTitle(), board.getContent());  // 각 셀에 들어갈 값
        values.add(row);
        googleService.writeToSheet(range, values);
    }
}
