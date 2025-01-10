package tabom.myhands.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tabom.myhands.domain.user.entity.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(String id);

    @Query("SELECT MAX(CAST(SUBSTRING(CAST(u.employeeNum AS string), -2) AS int)) " +
            "FROM User u WHERE SUBSTRING(CAST(u.employeeNum AS string), 1, 8) = :joinedDate")
    Integer findMaxSuffixByJoinedDate(@Param("joinedDate") String joinedDate);

    boolean existsById(String id);

    Optional<User> findByUserId(Long userId);

}
