package com.tablelog.tablelogback.domain.board_comment.service;

import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentListResponseDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentReadResponseDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.user.entity.User;

import java.io.IOException;

public interface BoardCommentService {
    void createBoardComment(BoardCommentCreateServiceRequestDto requestDto, Long boardId,
                User user, Long boardCommentId) throws IOException;
    void updateBoardComment(BoardCommentUpdateServiceRequestDto requestDto, User user,
                            Long boardId, Long boardCommentId) throws IOException;
    void deleteBoardComment(Long boardId, Long boardCommentId, User user) throws IOException;
    BoardCommentReadResponseDto readBoardComment(Long boardId, Long boardCommentId);
    BoardCommentListResponseDto readAllBoardComment(Long boardId, int pageNum);
    BoardCommentListResponseDto readAllBoardCommentByDesc(Long boardId, int pageNum);
    BoardCommentListResponseDto readAllBoardCommentReply(Long boardId, Long boardCommentId, int pageNum);
}
