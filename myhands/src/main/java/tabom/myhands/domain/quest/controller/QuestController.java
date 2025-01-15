package tabom.myhands.domain.quest.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tabom.myhands.common.properties.ResponseProperties;
import tabom.myhands.common.response.DtoResponse;
import tabom.myhands.domain.quest.dto.QuestRequest;
import tabom.myhands.domain.quest.dto.QuestResponse;
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
    public ResponseEntity<DtoResponse<QuestResponse>> createWeekCountJobQuest(@RequestBody QuestRequest.JobQuest request) {
        Quest weekCountJobQuest = questService.createWeekCountJobQuest(request);
        QuestResponse response = QuestResponse.from(weekCountJobQuest);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.CREATED, responseProperties.getSuccess(), response));
    }

    @GetMapping("/job")
    public ResponseEntity<DtoResponse<QuestResponse>> getWeekCountJobQuest(@RequestParam String departmentName,
                                                                           @RequestParam Integer jobGroup,
                                                                           @RequestParam Integer weekCount) {
        QuestRequest.JobQuest request = new QuestRequest.JobQuest(departmentName, jobGroup, weekCount);
        QuestResponse response = questService.getWeekCountJobQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @PatchMapping("/job")
    public ResponseEntity<DtoResponse<QuestResponse>> updateWeekCountJobQuest(@RequestBody QuestRequest.UpdateJobQuest request) throws FirebaseMessagingException {
        QuestResponse response = questService.updateWeekCountJobQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @GetMapping("/leader")
    public ResponseEntity<DtoResponse<QuestResponse>> getLeaderQuest(@RequestParam Integer month,
                                                                     @RequestParam Integer employeeNum,
                                                                     @RequestParam String name,
                                                                     @RequestParam String questName,
                                                                     @RequestParam String grade,
                                                                     @RequestParam Integer expAmount) {
        QuestRequest.LeaderQuest request = new QuestRequest.LeaderQuest(month, employeeNum, name, questName, grade, expAmount);
        QuestResponse response = questService.getLeaderQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @PatchMapping("/leader")
    public ResponseEntity<DtoResponse<QuestResponse>> updateLeaderQuest(@RequestBody QuestRequest.UpdateLeaderQuest request) throws FirebaseMessagingException {
        QuestResponse response = questService.updateLeaderQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @GetMapping
    public ResponseEntity<DtoResponse<List<QuestResponse>>> getQuests(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<QuestResponse> quests = userQuestService.getQuests(userId);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), quests));
    }

    @GetMapping("/completelist")
    public ResponseEntity<DtoResponse<QuestResponse.QuestPage>> getCompletedQuests(HttpServletRequest request,
                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "10") int size) {
        QuestResponse.QuestPage response = userQuestService.getCompletedQuest(request, page, size);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @GetMapping("/company")
    public ResponseEntity<DtoResponse<QuestResponse>> getCompanyQuest(@RequestParam Integer month,
                                                                      @RequestParam Integer day,
                                                                      @RequestParam Integer employeeNum,
                                                                      @RequestParam String name,
                                                                      @RequestParam String projectName,
                                                                      @RequestParam Integer expAmount) {
        QuestRequest.CompanyQuest request = new QuestRequest.CompanyQuest(month, day, employeeNum, name, projectName, expAmount);
        QuestResponse response = questService.getCompanyQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @PatchMapping("/company")
    public ResponseEntity<DtoResponse<QuestResponse>> updateCompanyQuest(@RequestBody QuestRequest.UpdateCompanyQuest request) throws FirebaseMessagingException {
        QuestResponse response = questService.updateCompanyQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @GetMapping("/hr")
    public ResponseEntity<DtoResponse<QuestResponse>> getHRQuest(@RequestParam Boolean isFirstHalf,
                                                                 @RequestParam Integer employeeNum,
                                                                 @RequestParam String name,
                                                                 @RequestParam String grade,
                                                                 @RequestParam Integer expAmount) {
        QuestRequest.HRQuest request = new QuestRequest.HRQuest(isFirstHalf, employeeNum, name, grade, expAmount);
        QuestResponse response = questService.getHRQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @PatchMapping("/hr")
    public ResponseEntity<DtoResponse<QuestResponse>> updateHRQuest(@RequestBody QuestRequest.UpdateHRQuest request) {
        QuestResponse response = questService.updateHRQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @GetMapping("/calendar")
    public ResponseEntity<DtoResponse<QuestResponse.QuestCalendar>> getQuestCalendar(HttpServletRequest servletRequest, @RequestBody QuestRequest.QuestCalendar request) {
        QuestResponse.QuestCalendar response = questService.getQuestCalendar(servletRequest, request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @GetMapping("/calendar-ios")
    public ResponseEntity<DtoResponse<QuestResponse.QuestCalendar>> getQuestCalendarByParam(HttpServletRequest servletRequest, @RequestParam Integer year, @RequestParam Integer month) {
        QuestResponse.QuestCalendar response = questService.getQuestCalendarByParam(servletRequest, year, month);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }


    @GetMapping("/stats")
    public ResponseEntity<DtoResponse<QuestResponse.QuestStats>> getQuestStats(HttpServletRequest request) {
        QuestResponse.QuestStats response = questService.getQuestStats(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }
}
