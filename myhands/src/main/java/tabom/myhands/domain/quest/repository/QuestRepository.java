package tabom.myhands.domain.quest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tabom.myhands.domain.quest.entity.Quest;

import java.util.Optional;

public interface QuestRepository extends JpaRepository<Quest, Long> {

    Optional<Quest> findByQuestId(Long questId);
}
