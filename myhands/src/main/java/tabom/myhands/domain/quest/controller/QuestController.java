package tabom.myhands.domain.quest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tabom.myhands.common.properties.ResponseProperties;
import tabom.myhands.common.response.DtoResponse;
import tabom.myhands.domain.quest.dto.QuestRequest;
import tabom.myhands.domain.quest.dto.QuestResponse;
import tabom.myhands.domain.quest.dto.UserQuestRequest;
import tabom.myhands.domain.quest.dto.UserQuestResponse;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.quest.service.QuestService;
import tabom.myhands.domain.quest.service.UserQuestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/quest")
public class QuestController {

    private final ResponseProperties responseProperties;
    private final QuestService questService;
    private final UserQuestService userQuestService;

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

    @PostMapping("/job")
    public ResponseEntity<DtoResponse<List<UserQuestResponse>>> createWeekCountJobQuest(@RequestBody UserQuestRequest.CreateJobQuest request) {
        Quest weekCountJobQuest = questService.createWeekCountJobQuest(request.getWeekCount());
        List<UserQuestResponse> jobQuest = userQuestService.createJobQuest(request.getDepartmentId(), weekCountJobQuest);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.CREATED, responseProperties.getSuccess(), jobQuest));
    }

    @GetMapping
    public ResponseEntity<DtoResponse<List<QuestResponse>>> getQuests(@RequestParam Long userId) {
        List<QuestResponse> quests = userQuestService.getQuests(userId);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), quests));
    }

    @GetMapping("/completelist")
    public ResponseEntity<DtoResponse<List<QuestResponse>>> getCompletedQuests(@RequestParam Long userId) {
        List<QuestResponse> completedQuest = userQuestService.getCompletedQuest(userId);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), completedQuest));
    }
}
