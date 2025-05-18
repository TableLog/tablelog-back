package com.tablelog.tablelogback.domain.board_like.service;

public interface BoardLikeService {
    void createBoardLike (Long boardId, Long userId);
    void deleteBoardLike(Long boardId, Long userId);
    Boolean hasBoardLiked(Long boardId, Long userId);
    Long getBoardLikeCountByBoard(Long boardId);
//    Long getAllLikes (Long boardId);
}
