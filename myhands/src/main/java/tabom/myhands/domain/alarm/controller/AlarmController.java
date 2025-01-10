package tabom.myhands.domain.alarm.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tabom.myhands.common.properties.ResponseProperties;
import tabom.myhands.common.response.DtoResponse;
import tabom.myhands.common.response.MessageResponse;
import tabom.myhands.domain.alarm.dto.AlarmResponse;
import tabom.myhands.domain.alarm.service.AlarmService;

@RestController
@RequiredArgsConstructor
@RequestMapping("alarm")
public class AlarmController {
    private final ResponseProperties responseProperties;
    private final AlarmService alarmService;

    @DeleteMapping("")
    public ResponseEntity<MessageResponse> delete(HttpServletRequest request) {
        alarmService.deleteAlarm((Long) request.getAttribute("userId"));
        return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.of(HttpStatus.OK, responseProperties.getSuccess()));
    }

    @GetMapping("")
    public ResponseEntity<DtoResponse<AlarmResponse.AlarmList>> list(HttpServletRequest request) {
        AlarmResponse.AlarmList response = alarmService.getAlarmList((Long) request.getAttribute("userId"));

        if(response == null) {
            ResponseEntity.status(HttpStatus.OK).body(DtoResponse.of(HttpStatus.OK, responseProperties.getFail(),null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(DtoResponse.of(HttpStatus.OK, responseProperties.getSuccess(),response));
    }

//    @GetMapping("")
//    public ResponseEntity<MessageResponse> testAlarm() throws FirebaseMessagingException {
//        alarmService.sendMessage("f_9TvC02n0dFlU6d1viTFs:APA91bFcEfGNSJ84vkLSNQ3eecjD0owD49uRagkFzWh0V413_eQVeceJmY-tbz-zS8Sovrd0dR0zoLWxw9-BWQCDRUf1ap_UxR_EFXu0pFpybDPgSGQmcTQ",
//                "푸쉬푸쉬 테스트", "푸쉬푸쉬 베이베 내 푸쉬 받아줘");
//        return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.of(HttpStatus.OK, responseProperties.getSuccess()));
//    }
}
