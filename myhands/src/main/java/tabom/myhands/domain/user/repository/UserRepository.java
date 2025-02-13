package tabom.myhands.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tabom.myhands.domain.user.entity.Department;
import tabom.myhands.domain.user.entity.User;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(String id);

    @Query("SELECT MAX(CAST(SUBSTRING(CAST(u.employeeNum AS string), -2) AS int)) " +
            "FROM User u WHERE SUBSTRING(CAST(u.employeeNum AS string), 1, 8) = :joinedDate")
    Integer findMaxSuffixByJoinedDate(@Param("joinedDate") String joinedDate);

    boolean existsById(String id);

    Optional<User> findByUserId(Long userId);

    List<User> findUsersByDepartment(Department department);

    @Query(value = "SELECT * FROM user ORDER BY name, department_id, employee_num", nativeQuery = true)
    List<User> findAllUser();

    boolean existsByEmployeeNum(Integer num);

    List<User> findAll();

    List<User> findUsersByDepartmentAndJobGroup(Department department, Integer jobGroup);

    Optional<User> findUserByEmployeeNumAndName(Integer employeeNum, String name);

    @Query("SELECT u FROM User u WHERE u.googleId = :googleId ORDER BY u.employeeNum DESC")
    List<User> findByGoogleIdOrderByEmployeeNumDesc(@Param("googleId") Long googleId);

    boolean existsByEmployeeNum(int employeeNum);

    @Query("SELECT MAX(u.googleId) FROM User u WHERE u.googleId IS NOT NULL")
    Long findMaxGoogleId();
}
