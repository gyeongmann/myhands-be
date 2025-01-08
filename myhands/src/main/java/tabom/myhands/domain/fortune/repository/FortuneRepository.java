package tabom.myhands.domain.fortune.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tabom.myhands.domain.fortune.entity.Fortune;

public interface FortuneRepository extends JpaRepository<Fortune, Long>{
    Fortune findByFortuneId(int fortuneId);
}
