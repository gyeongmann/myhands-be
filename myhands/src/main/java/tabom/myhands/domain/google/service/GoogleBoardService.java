package tabom.myhands.domain.google.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import tabom.myhands.domain.board.dto.BoardRequest;
import tabom.myhands.domain.board.entity.Board;

public interface GoogleBoardService {
    void create(Long userId, BoardRequest.Edit requestDto) throws FirebaseMessagingException;

    void createBoardToSheet(Board board);
}
