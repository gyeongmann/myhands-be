package tabom.myhands.domain.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tabom.myhands.common.properties.ResponseProperties;
import tabom.myhands.common.response.MessageResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class SeverController {
    private final ResponseProperties responseProperties;

    @GetMapping("")
    public ResponseEntity<MessageResponse> testHealth() {
        return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.of(HttpStatus.OK, responseProperties.getSuccess()));
    }
}
