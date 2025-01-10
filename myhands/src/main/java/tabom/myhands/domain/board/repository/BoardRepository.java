package tabom.myhands.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tabom.myhands.domain.board.entity.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByBoardId(Long id);

    @Query(value = "SELECT * FROM board ORDER BY created_at DESC LIMIT :size", nativeQuery = true)
    List<Board> findFirstPage(@Param("size") int size);
}
