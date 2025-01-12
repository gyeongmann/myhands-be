package tabom.myhands.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tabom.myhands.domain.user.entity.Department;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    Optional<Department> findDepartmentByDepartmentId(Integer departmentId);

    Optional<Department> findDepartmentByName(String departmentName);
}
