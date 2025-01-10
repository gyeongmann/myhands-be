package tabom.myhands.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tabom.myhands.domain.board.entity.Board;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByBoardId(Long id);
}
