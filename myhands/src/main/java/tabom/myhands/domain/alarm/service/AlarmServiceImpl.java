package tabom.myhands.domain.alarm.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import com.google.firebase.messaging.Notification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tabom.myhands.domain.alarm.dto.AlarmResponse;
import tabom.myhands.domain.alarm.entity.Alarm;
import tabom.myhands.domain.alarm.repository.AlarmRepository;
import tabom.myhands.domain.user.entity.User;
import tabom.myhands.domain.user.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void deleteAlarm(Long userId) {
        User user = userRepository.findByUserId(userId).get();
        alarmRepository.deleteAllByUser(user);
    }

    public AlarmResponse.AlarmList getAlarmList(Long userId) {
        User user = userRepository.findByUserId(userId).get();
        List<AlarmResponse.CreateAlarm> recentList = new ArrayList<>();
        List<AlarmResponse.CreateAlarm> oldList = new ArrayList<>();

        List<Alarm> alarmList = alarmRepository.findAllByUser(user);
        Collections.sort(alarmList, (o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()));

        for(Alarm a : alarmList) {
            boolean lastDay = a.getCreatedAt().isBefore(LocalDate.now().atStartOfDay());
            String createdAt = formatCreatedAt(a.getCreatedAt(), lastDay);
            AlarmResponse.CreateAlarm ca = AlarmResponse.CreateAlarm.build(a, createdAt);

            if(lastDay) {
                oldList.add(ca);
            } else {
                recentList.add(ca);
            }
        }

        return AlarmResponse.AlarmList.build(recentList, oldList);
    }

    private String formatCreatedAt(LocalDateTime d, boolean lastDay) {
        if(lastDay) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. M. d.");
            return d.format(formatter);
        }

        int diffMin = (int) Duration.between(d, LocalDateTime.now()).toMinutes();

        if(diffMin > 60) {
            return diffMin / 60 + "시간 전";
        }

        if(diffMin > 10) {
            return diffMin / 10 + "0분 전";
        }

        return diffMin + "분 전";
    }


    public void sendMessage(User user, Alarm alarm) throws FirebaseMessagingException {
        String title = "공지사항";
        String body = alarm.getTitle();

        if(!alarm.isCategory()) {
            title = "경험치 획득";
            body = body + "\n" + alarm.getExp() + " do를 획득하셨습니다.";
        }

        Message message = Message.builder()
                .setToken(user.getDeviceToken())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
    }

}
