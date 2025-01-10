package tabom.myhands.domain.alarm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tabom.myhands.domain.alarm.entity.Alarm;
import tabom.myhands.domain.user.entity.User;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
//    List<Alarm> findAllByUserOrderByCreateAtDesc(User user);
    List<Alarm> findAllByUser(User user);
    void deleteAllByUser(User user);
}
