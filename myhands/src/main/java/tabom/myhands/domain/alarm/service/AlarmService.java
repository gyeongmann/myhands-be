package tabom.myhands.domain.alarm.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import tabom.myhands.domain.alarm.dto.AlarmResponse;

public interface AlarmService {
    void deleteAlarm(Long userId);
    AlarmResponse.AlarmList getAlarmList(Long userId);

//    void sendMessage(String token, String title, String body) throws FirebaseMessagingException;
}
