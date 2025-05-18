package com.tablelog.tablelogback.domain.board_like.service;

public interface BoardLikeService {
    void createBoardLike (Long boardId, Long userId);
    void deleteBoardLike(Long boardId, Long userId);
//    Long getAllLikes (Long boardId);
//    Boolean isLike(Long boardId, Long user);
}
