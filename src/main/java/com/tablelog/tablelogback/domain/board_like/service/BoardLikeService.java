package com.tablelog.tablelogback.domain.board_like.service;

import com.tablelog.tablelogback.domain.board.dto.service.BoardListResponseDto;

public interface BoardLikeService {
    void createBoardLike (Long boardId, Long userId);
    void deleteBoardLike(Long boardId, Long userId);
    Boolean hasBoardLiked(Long boardId, Long userId);
    Long getBoardLikeCountByBoard(Long boardId);
    BoardListResponseDto getMyLikedBoards(Long userId, int pageNum);
}
