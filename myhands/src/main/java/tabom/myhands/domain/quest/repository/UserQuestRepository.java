package tabom.myhands.domain.quest.repository;

import org.springframework.data.repository.CrudRepository;
import tabom.myhands.domain.quest.entity.UserQuest;
import tabom.myhands.domain.user.entity.User;

import java.util.List;

public interface UserQuestRepository extends CrudRepository<UserQuest, Long> {

    List<UserQuest> findByUser(User user);
}
