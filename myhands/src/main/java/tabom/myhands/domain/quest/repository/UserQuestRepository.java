package tabom.myhands.domain.quest.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.quest.entity.UserQuest;
import tabom.myhands.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface UserQuestRepository extends CrudRepository<UserQuest, Long> {

    List<UserQuest> findByUser(User user);

    @Query("SELECT uq FROM UserQuest uq JOIN FETCH uq.quest WHERE uq.user = :user")
    List<UserQuest> findByUserWithQuest(@Param("user") User user);

    @Query("SELECT uq.user FROM UserQuest uq WHERE uq.quest.id = :questId")
    List<User> findUsersByQuestId(@Param("questId") Long questId);

    @Query("SELECT uq FROM UserQuest uq " +
            "JOIN FETCH uq.quest q " +
            "WHERE uq.user = :user " +
            "AND YEAR(q.completedAt) = :year")
    List<UserQuest> findByUserAndQuestCompletedYear(@Param("user") User user, @Param("year") int year);

    @Query("SELECT uq FROM UserQuest uq " +
            "JOIN FETCH uq.quest q " +
            "WHERE uq.user = :user " +
            "AND q.completedAt < :endDate")
    List<UserQuest> findByUserAndQuestCompletedBeforeDate(@Param("user") User user, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT uq FROM UserQuest uq " +
            "JOIN FETCH uq.quest q " +
            "WHERE uq.user = :user " +
            "AND q.expAmount > 0 " +
            "AND q.isCompleted = true " +
            "AND q.completedAt < CURRENT_TIMESTAMP " +
            "ORDER BY q.completedAt DESC")
    List<UserQuest> findMostRecentCompletedQuest(@Param("user") User user, Pageable pageable);

    @Query("SELECT uq.quest FROM UserQuest uq " +
            "WHERE uq.user = :user " +
            "AND uq.quest.completedAt IS NOT NULL " +  // completedAt이 null이 아닌 경우
            "AND FUNCTION('YEAR', uq.quest.completedAt) = :year " +
            "AND FUNCTION('MONTH', uq.quest.completedAt) = :month")
    List<Quest> findQuestsByUserAndCompletedAtYearAndMonth(@Param("user") User user,
                                                           @Param("year") int year,
                                                           @Param("month") int month);

}
