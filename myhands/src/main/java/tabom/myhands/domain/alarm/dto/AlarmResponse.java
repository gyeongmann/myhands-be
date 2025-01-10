package tabom.myhands.domain.alarm.dto;

import lombok.*;
import tabom.myhands.domain.alarm.entity.Alarm;
import tabom.myhands.domain.user.dto.UserResponse;

import java.util.List;

public class AlarmResponse {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateAlarm {
        private boolean category;
        private String createdAt;
        private String title;
        private Long boardId;
        private int exp;

        public static AlarmResponse.CreateAlarm build(Alarm alarm, String createdAt){
            return CreateAlarm.builder()
                    .category(alarm.isCategory())
                    .createdAt(createdAt)
                    .title(alarm.getTitle())
                    .boardId(alarm.getBoardId())
                    .exp(alarm.getExp())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlarmList {
        private List<CreateAlarm> recentAlarmList;
        private List<CreateAlarm> oldAlarmList;

        public static AlarmResponse.AlarmList build(List<CreateAlarm> recentAlarmList, List<CreateAlarm> oldAlarmList) {
            return AlarmList.builder()
                    .recentAlarmList(recentAlarmList)
                    .oldAlarmList(oldAlarmList)
                    .build();
        }
    }
}
