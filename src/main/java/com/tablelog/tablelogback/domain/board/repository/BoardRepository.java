package com.tablelog.tablelogback.domain.board.repository;

import com.tablelog.tablelogback.domain.board.dto.service.BoardReadByAdminResponseDto;
import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByIdAndUser(Long id, String user);
    List<Board> findAllByUser(String user);
    Slice<Board> findAllBy(Pageable pageable);
    Slice<Board> findAllByOrderByIdDesc(Pageable pageable);
    Slice<Board> findAllByOrderByIdAsc(Pageable pageable);
    Slice<Board> findAllByUserOrderByUserAsc(String user, Pageable pageable);
    void deleteAllByUser(String user);
    @Query(value = """
        SELECT DATE(created_at) AS date, COUNT(*) AS count
          FROM tb_board
         WHERE created_at >= :startDate
         GROUP BY DATE(created_at)
         ORDER BY DATE(created_at) DESC
    """, nativeQuery = true)
    List<Object[]> findDailyCreatedCount(@Param("startDate") LocalDateTime startDate);

//    @Query("""
//        SELECT new com.tablelog.tablelogback.domain.board.dto.BoardReadByAdminResponseDto(
//            a.id, a.user, b.userName, a.content, a.createdAt
//        )
//        FROM Board a
//        JOIN User b ON a.user = b.nickname
//        WHERE a.user LIKE %:keyword%
//           OR b.userName LIKE %:keyword%
//        ORDER BY a.createdAt DESC
//    """)
//    Slice<BoardReadByAdminResponseDto> searchBoardsByUserNameOrNickname(@Param("keyword") String keyword, Pageable pageable);
}

