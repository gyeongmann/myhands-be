package tabom.myhands.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tabom.myhands.domain.user.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}
