package com.tablelog.tablelogback.domain.board_comment.repository;

import com.tablelog.tablelogback.domain.board_comment.entity.BoardComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    List<BoardComment> findAllByUser(String user);
    Optional<BoardComment> findByBoardIdAndIdAndUser(String boardId, Long id, String user);
    Slice<BoardComment> findAllByBoardId(String boardId, Pageable pageable);
    Slice<BoardComment> findAllByBoardIdOrderByCreatedAtDesc(String boardId, Pageable pageable);
    Slice<BoardComment> findAllByBoardIdAndCommentId(String boardId, Long commentId, Pageable pageable);
    Slice<BoardComment> findAllByCommentId(Long commentId, Pageable pageable);
    Integer countByBoardId(String boardId);
    Integer countByCommentId(Long commentId);
}
