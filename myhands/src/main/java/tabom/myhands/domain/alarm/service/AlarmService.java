package tabom.myhands.domain.alarm.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import tabom.myhands.domain.alarm.dto.AlarmResponse;
import tabom.myhands.domain.board.entity.Board;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.user.entity.User;

public interface AlarmService {
    void deleteAlarm(Long userId);
    AlarmResponse.AlarmList getAlarmList(Long userId);
    void createBoardAlarm(Board board) throws FirebaseMessagingException;
    void createExpAlarm(User user, Quest quest, boolean updateAlarm) throws FirebaseMessagingException;
}
