package tabom.myhands.domain.alarm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tabom.myhands.domain.alarm.entity.Alarm;
import tabom.myhands.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;


public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByUserOrderByCreatedAtDesc(User user);
    @Modifying
    @Query("DELETE FROM Alarm a WHERE a.user = :user AND a.createdAt >= :startOfDay")
    void deleteTodayAlarmsByUser(@Param("user") User user, @Param("startOfDay") LocalDateTime startOfDay);

    @Modifying
    @Query("DELETE FROM Alarm a WHERE a.user = :user AND a.createdAt < :startOfDay")
    void deleteOldAlarms(@Param("user") User user, @Param("startOfDay") LocalDateTime startOfDay);

    @Query("SELECT a FROM Alarm a WHERE a.questId = :questId AND a.user = :user")
    List<Alarm> findAllByQuestIdAndUser(@Param("questId") Long questId, @Param("user") User user);
}
