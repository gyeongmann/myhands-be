package tabom.myhands.domain.google.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tabom.myhands.common.properties.ResponseProperties;
import tabom.myhands.common.response.MessageResponse;
import tabom.myhands.domain.board.dto.BoardRequest;
import tabom.myhands.domain.google.service.GoogleBoardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/google/board")
public class GoogleBoardController {
    private final ResponseProperties responseProperties;
    private final GoogleBoardService googleBoardService;

    @PostMapping("")
    public ResponseEntity<MessageResponse> create(@RequestBody BoardRequest.Edit requestDto) {
        Long userId = 1L;
        googleBoardService.create(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.of(HttpStatus.CREATED, responseProperties.getSuccess()));
    }
}
