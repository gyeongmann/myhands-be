package tabom.myhands.domain.quest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tabom.myhands.domain.quest.entity.Quest;

import java.util.Optional;

public interface QuestRepository extends JpaRepository<Quest, Long> {

    Optional<Quest> findByQuestId(Long questId);

    @Query("SELECT q FROM Quest q WHERE q.name = CONCAT(:departmentName, ' 직무그룹', :jobGroupNumber, ' ', :weekCount, '주차')")
    Optional<Quest> findByFormattedName(@Param("departmentName") String departmentName,
                                        @Param("jobGroupNumber") Integer jobGroupNumber,
                                        @Param("weekCount") Integer weekCount);
}
