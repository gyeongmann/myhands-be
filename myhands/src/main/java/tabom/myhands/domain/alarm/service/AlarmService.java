package tabom.myhands.domain.alarm.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import tabom.myhands.domain.alarm.dto.AlarmResponse;
import tabom.myhands.domain.alarm.entity.Alarm;
import tabom.myhands.domain.user.entity.User;

public interface AlarmService {
    void deleteAlarm(Long userId);
    AlarmResponse.AlarmList getAlarmList(Long userId);
    void sendMessage(User user, Alarm alarm) throws FirebaseMessagingException;
}
