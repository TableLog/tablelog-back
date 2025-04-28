package com.tablelog.tablelogback.domain.board_comment.repository;

import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.board_comment.entity.BoardComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    Optional<BoardComment> findByIdAndUser(Long id, String user);
//    List<Board> findByName(String name);
//
    Optional<BoardComment> findByBoardidAndIdAndUser(String board_id,Long id,String user);

    Slice<BoardComment> findAllByBoardid(String board_Id, Pageable pageable);

}
