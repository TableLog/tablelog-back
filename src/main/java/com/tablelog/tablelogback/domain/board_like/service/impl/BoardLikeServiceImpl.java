package com.tablelog.tablelogback.domain.board_like.service.impl;

import com.tablelog.tablelogback.domain.board.dto.service.BoardListResponseDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardReadResponseDto;
import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.board.exception.BoardErrorCode;
import com.tablelog.tablelogback.domain.board.exception.NotFoundBoardException;
import com.tablelog.tablelogback.domain.board.mapper.entity.BoardEntityMapper;
import com.tablelog.tablelogback.domain.board.repository.BoardRepository;
import com.tablelog.tablelogback.domain.board_like.entity.BoardLike;
import com.tablelog.tablelogback.domain.board_like.exception.AlreadyExistsBoardLikeException;
import com.tablelog.tablelogback.domain.board_like.exception.BoardLikeErrorCode;
import com.tablelog.tablelogback.domain.board_like.exception.NotFoundBoardLikeException;
import com.tablelog.tablelogback.domain.board_like.repository.BoardLikeRepository;
import com.tablelog.tablelogback.domain.board_like.service.BoardLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardLikeServiceImpl implements BoardLikeService {
    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;
    private final BoardEntityMapper boardEntityMapper;

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
        return boardLikeRepository.existsByBoardAndUser(boardId, userId);
    }

    @Override
    public Long getBoardLikeCountByBoard(Long boardId) {
        Board board = findBoard(boardId);
        return boardLikeRepository.countByBoard(boardId);
    }

    @Override
    public BoardListResponseDto getMyLikedBoards(Long userId, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Board> boards = boardLikeRepository.findAllByUser(userId, pageRequest);
        List<BoardReadResponseDto> responseDtos = boardEntityMapper.toBoardReadResponseDtos(boards.getContent());
        return new BoardListResponseDto(responseDtos, boards.hasNext());
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
