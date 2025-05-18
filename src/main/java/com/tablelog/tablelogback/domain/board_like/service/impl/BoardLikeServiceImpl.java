package com.tablelog.tablelogback.domain.board_like.service.impl;

import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.board.exception.BoardErrorCode;
import com.tablelog.tablelogback.domain.board.exception.NotFoundBoardException;
import com.tablelog.tablelogback.domain.board.repository.BoardRepository;
import com.tablelog.tablelogback.domain.board_like.entity.BoardLike;
import com.tablelog.tablelogback.domain.board_like.exception.AlreadyExistsBoardLikeException;
import com.tablelog.tablelogback.domain.board_like.exception.BoardLikeErrorCode;
import com.tablelog.tablelogback.domain.board_like.repository.BoardLikeRepository;
import com.tablelog.tablelogback.domain.board_like.service.BoardLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BoardLikeServiceImpl implements BoardLikeService {
    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void createLike(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        if(boardLikeRepository.existsByBoardAndUser(board.getId(), userId)){
            throw new AlreadyExistsBoardLikeException(BoardLikeErrorCode.ALREADY_EXIST_BOARD_LIKE);
        }
        BoardLike like = BoardLike.builder()
                .user(userId)
                .board(board.getId())
                .build();
        boardLikeRepository.save(like);
    }

}
