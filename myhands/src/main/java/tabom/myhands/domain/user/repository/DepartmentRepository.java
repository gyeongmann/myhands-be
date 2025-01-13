package tabom.myhands.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tabom.myhands.domain.user.entity.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    Optional<Department> findDepartmentByDepartmentId(Integer departmentId);

    Optional<Department> findDepartmentByName(String departmentName);

    @Query("SELECT d.departmentId, COUNT(u) FROM Department d LEFT JOIN User u ON d.departmentId = u.department.departmentId GROUP BY d.departmentId")
    List<Object[]> countUsersByDepartment();
}
