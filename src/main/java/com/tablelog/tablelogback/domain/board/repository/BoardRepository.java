package com.tablelog.tablelogback.domain.board.repository;

import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByIdAndUser(Long id, String user);
//    List<Board> findByName(String name);
//
    Slice<Board> findAllBy(Pageable pageable);
    Slice<Board> findAllByOrderByIdDesc(Pageable pageable);
    Slice<Board> findAllByOrderByIdAsc(Pageable pageable);
    Slice<Board> findAllByUserOrderByUserAsc(String user, Pageable pageable);
}

