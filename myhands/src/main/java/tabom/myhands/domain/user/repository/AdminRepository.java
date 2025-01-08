package tabom.myhands.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tabom.myhands.domain.user.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
