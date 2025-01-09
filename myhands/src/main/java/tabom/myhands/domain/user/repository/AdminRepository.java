package tabom.myhands.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tabom.myhands.domain.user.entity.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findById(String id);
}
