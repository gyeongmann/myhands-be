package tabom.myhands.domain.quest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tabom.myhands.common.properties.ResponseProperties;
import tabom.myhands.common.response.DtoResponse;
import tabom.myhands.domain.quest.dto.QuestRequest;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.quest.service.QuestService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quest")
public class QuestController {

    private final ResponseProperties responseProperties;
    private final QuestService questService;

    @PostMapping
    public ResponseEntity<DtoResponse<Quest>> createQuest(@RequestBody QuestRequest.Create request) {
        Quest quest = questService.createQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.CREATED, responseProperties.getSuccess(), quest));
    }

    @PatchMapping
    public ResponseEntity<DtoResponse<Quest>> updateQuest(@RequestBody QuestRequest.Complete request) {
        Quest quest = questService.updateQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), quest));
    }

}
