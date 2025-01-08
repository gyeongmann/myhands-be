package tabom.myhands.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tabom.myhands.common.response.MessageResponse;
import tabom.myhands.domain.user.dto.UserRequest;
import tabom.myhands.domain.user.service.UserService;
import tabom.myhands.common.properties.ResponseProperties;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ResponseProperties responseProperties;

    @PostMapping(value ="/join")
    public ResponseEntity<MessageResponse> createUser(@RequestBody UserRequest.Join requestDto) {
        userService.join(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.of(HttpStatus.CREATED, responseProperties.getSuccess()));
    }
}