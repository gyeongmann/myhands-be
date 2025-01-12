package tabom.myhands.domain.alarm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tabom.myhands.domain.alarm.entity.Alarm;
import tabom.myhands.domain.user.entity.User;

import java.util.List;


public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByUserOrderByCreatedAtDesc(User user);
    @Modifying
    @Query("DELETE FROM Alarm a WHERE a.user = :user")
    void deleteAllByUser(@Param("user") User user);
}
