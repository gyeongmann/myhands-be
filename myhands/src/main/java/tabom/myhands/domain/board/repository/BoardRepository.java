package tabom.myhands.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tabom.myhands.domain.board.entity.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByBoardId(Long id);

    Optional<Board> findByGoogleId(Long id);

    @Query(value = "SELECT * FROM board ORDER BY created_at DESC LIMIT :size", nativeQuery = true)
    List<Board> findFirstPage(@Param("size") int size);

    @Query(value = "SELECT * FROM board ORDER BY created_at DESC LIMIT :size", nativeQuery = true)
    List<Board> findAllFirstPage(@Param("size") int size);

    @Query(value = "SELECT * FROM board WHERE board_id < :lastId ORDER BY created_at DESC LIMIT :size", nativeQuery = true)
    List<Board> findAllLastId(@Param("lastId") Long lastId, @Param("size") int size);

    @Query(value = "SELECT * FROM board WHERE title LIKE CONCAT('%', :word, '%') ORDER BY created_at DESC LIMIT :size", nativeQuery = true)
    List<Board> findWordFirstPage(@Param("word") String word, @Param("size") int size);

    @Query(value = "SELECT * FROM board WHERE title LIKE CONCAT('%', :word, '%') AND board_id < :lastId ORDER BY created_at DESC LIMIT :size", nativeQuery = true)
    List<Board> findWordLastId(@Param("word") String word, @Param("lastId") Long lastId, @Param("size") int size);

    @Query("SELECT MAX(b.googleId) FROM Board b WHERE b.googleId IS NOT NULL")
    Long findMaxGoogleId();
}
