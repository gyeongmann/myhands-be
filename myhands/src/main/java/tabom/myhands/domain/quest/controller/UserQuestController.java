package tabom.myhands.domain.quest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tabom.myhands.common.properties.ResponseProperties;
import tabom.myhands.common.response.MessageResponse;
import tabom.myhands.domain.quest.dto.UserQuestRequest;
import tabom.myhands.domain.quest.service.UserQuestService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-quest")
public class UserQuestController {

    private final UserQuestService userQuestService;
    private final ResponseProperties responseProperties;

    @PostMapping
    public ResponseEntity<MessageResponse> createUserQuest(@RequestBody UserQuestRequest.Create request) {
        userQuestService.createUserQuest(request);
        return ResponseEntity.ok(new MessageResponse(HttpStatus.OK, responseProperties.getSuccess()));
    }
}
