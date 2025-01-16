package tabom.myhands.domain.google.controller;

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
import tabom.myhands.domain.google.service.GoogleUserService;
import tabom.myhands.domain.user.dto.UserRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/google")
public class GoogleController {
    private final ResponseProperties responseProperties;
    private final GoogleBoardService googleBoardService;
    private final GoogleUserService googleUserService;

    @PostMapping("/board")
    public ResponseEntity<MessageResponse> createBoard(@RequestBody BoardRequest.Edit requestDto) {
        Long userId = 1L;
        googleBoardService.create(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.of(HttpStatus.CREATED, responseProperties.getSuccess()));
    }

    @PostMapping("/user")
    public ResponseEntity<MessageResponse> createUser(@RequestBody UserRequest.GoogleJoin requestDto) {
        googleUserService.createUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.of(HttpStatus.CREATED, responseProperties.getSuccess()));
    }
}
