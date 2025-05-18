package com.tablelog.tablelogback.domain.board_like.repository;

import com.tablelog.tablelogback.domain.board_like.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByBoardAndUser(Long board, Long user);
    Boolean existsByBoardAndUser(Long board, Long user);
    Long countByBoard(Long board);
}
