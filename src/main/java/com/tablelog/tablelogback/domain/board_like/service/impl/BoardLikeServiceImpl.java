package com.tablelog.tablelogback.domain.board_like.service.impl;

import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.board.exception.BoardErrorCode;
import com.tablelog.tablelogback.domain.board.exception.NotFoundBoardException;
import com.tablelog.tablelogback.domain.board.repository.BoardRepository;
import com.tablelog.tablelogback.domain.board_like.entity.BoardLike;
import com.tablelog.tablelogback.domain.board_like.exception.AlreadyExistsBoardLikeException;
import com.tablelog.tablelogback.domain.board_like.exception.BoardLikeErrorCode;
import com.tablelog.tablelogback.domain.board_like.exception.NotFoundBoardLikeException;
import com.tablelog.tablelogback.domain.board_like.repository.BoardLikeRepository;
import com.tablelog.tablelogback.domain.board_like.service.BoardLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardLikeServiceImpl implements BoardLikeService {
    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;

    @Override
    public void createBoardLike(Long boardId, Long userId) {
        Board board = findBoard(boardId);
        if(boardLikeRepository.existsByBoardAndUser(board.getId(), userId)){
            throw new AlreadyExistsBoardLikeException(BoardLikeErrorCode.ALREADY_EXIST_BOARD_LIKE);
        }
        BoardLike like = BoardLike.builder()
                .user(userId)
                .board(board.getId())
                .build();
        boardLikeRepository.save(like);
    }

    @Override
    public void deleteBoardLike(Long boardId, Long userId) {
        Board board = findBoard(boardId);
        BoardLike boardLike = findBoardLike(boardId, userId);
        boardLikeRepository.delete(boardLike);
    }

    @Override
    public Boolean hasBoardLiked(Long boardId, Long userId) {
        Board board = findBoard(boardId);
        findBoardLike(boardId, userId);
        return boardLikeRepository.existsByBoardAndUser(boardId, userId);
    }

    private Board findBoard(Long boardId){
        return boardRepository.findById(boardId)
                .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
    }

    private BoardLike findBoardLike(Long boardId, Long userId){
        return boardLikeRepository.findByBoardAndUser(boardId, userId)
                .orElseThrow(() -> new NotFoundBoardLikeException(BoardLikeErrorCode.NOT_FOUND_BOARD_LIKE));
    }
}
