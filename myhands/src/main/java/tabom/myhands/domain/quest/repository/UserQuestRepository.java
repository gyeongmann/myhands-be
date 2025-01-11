package tabom.myhands.domain.quest.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import tabom.myhands.domain.quest.entity.UserQuest;
import tabom.myhands.domain.user.entity.User;

import java.util.List;

public interface UserQuestRepository extends CrudRepository<UserQuest, Long> {

    List<UserQuest> findByUser(User user);

    @Query("SELECT uq FROM UserQuest uq JOIN FETCH uq.quest WHERE uq.user = :user")
    List<UserQuest> findByUserWithQuest(@Param("user") User user);
}
