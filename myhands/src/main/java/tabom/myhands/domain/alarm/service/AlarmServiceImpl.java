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
import tabom.myhands.error.errorcode.UserErrorCode;
import tabom.myhands.error.exception.UserApiException;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void deleteAlarm(Long userId) {
        User user = userRepository.findById(userId).get();
        alarmRepository.deleteAllByUser(user);
    }

    public AlarmResponse.AlarmList getAlarmList(Long userId) {
        User user = userRepository.findById(userId).get();
        List<AlarmResponse.CreateAlarm> recentList = new ArrayList<>();
        List<AlarmResponse.CreateAlarm> oldList = new ArrayList<>();

        List<Alarm> alarmList = alarmRepository.findAllByUserOrderByCreateAtDesc(user);

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

    /*

    public void sendMessage(String token, String title, String body) throws FirebaseMessagingException {
//         String message = FirebaseMessaging.getInstance().send(Message.builder()
//                .setNotification(Notification.builder()
//                        .setTitle(title)
//                        .setBody(body)
//                        .build())
//                .setToken(token)  // 대상 디바이스의 등록 토큰
//                .build());


        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully sent message: " + response);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "myhandsFirebaseKey.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("<https://www.googleapis.com/auth/cloud-platform>"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

     */

}
