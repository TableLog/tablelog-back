package com.tablelog.tablelogback.domain.board_like.repository;

import com.tablelog.tablelogback.domain.board_like.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByBoardAndUser(Long user, Long board);
    Boolean existsByBoardAndUser(Long user, Long board);
//    Long countByBoardId(Long boardId);
}
