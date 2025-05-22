package com.tablelog.tablelogback.domain.board_like.repository;

import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.board_like.entity.BoardLike;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByBoardAndUser(Long board, Long user);
    @Query("SELECT b FROM Board b JOIN BoardLike l ON b.id = l.board " +
            "WHERE l.user = :userId")
    Slice<Board> findAllByUser(@Param("userId") Long userId, PageRequest pageRequest);
    Boolean existsByBoardAndUser(Long board, Long user);
    Long countByBoard(Long board);

}
