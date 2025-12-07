package com.tablelog.tablelogback.domain.board_comment.service.impl;

import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.board.exception.BoardErrorCode;
import com.tablelog.tablelogback.domain.board.exception.NotFoundBoardException;
import com.tablelog.tablelogback.domain.board.repository.BoardRepository;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentListResponseDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentReadResponseDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.board_comment.entity.BoardComment;
import com.tablelog.tablelogback.domain.board_comment.exception.BoardCommentErrorCode;
import com.tablelog.tablelogback.domain.board_comment.exception.NotFoundBoardCommentException;
import com.tablelog.tablelogback.domain.board_comment.mapper.entity.BoardCommentEntityMapper;
import com.tablelog.tablelogback.domain.board_comment.repository.BoardCommentRepository;
import com.tablelog.tablelogback.domain.board_comment.service.BoardCommentService;
import com.tablelog.tablelogback.domain.user.entity.User;
import com.tablelog.tablelogback.domain.user.exception.NotFoundUserException;
import com.tablelog.tablelogback.domain.user.exception.UserErrorCode;
import com.tablelog.tablelogback.domain.user.repository.UserRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardCommentCommentServiceImpl implements BoardCommentService {
    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardCommentEntityMapper boardCommentEntityMapper;
    private final UserRepository userRepository;

    @Override
    public void createBoardComment(final BoardCommentCreateServiceRequestDto boardCommentRequestDto,
            Long boardId, User user, Long boardCommentId
    ) throws IOException {
        Board board = findBoard(boardId);
        BoardComment boardComment;
        if(boardCommentId != null){
            boardComment = boardCommentEntityMapper.toBoardComment(boardCommentRequestDto, board, user, boardCommentId);
        } else{
            boardComment = boardCommentEntityMapper.toBoardComment(boardCommentRequestDto, board, user, null);
        }
        boardCommentRepository.save(boardComment);

    }

    @Override
    public void updateBoardComment(final BoardCommentUpdateServiceRequestDto boardCommentRequestDto,
                                   User user, Long boardId, Long boardCommentId
    ) throws IOException {
        Board board = findBoard(boardId);
        BoardComment boardComment = boardCommentRepository
                .findByBoardIdAndIdAndUser(board.getId().toString(), boardCommentId, user.getNickname())
                .orElseThrow(()-> new NotFoundBoardCommentException(BoardCommentErrorCode.NOT_FOUND_BOARDCOMMENT));
        boardComment.update(boardCommentRequestDto.content());
        boardCommentRepository.save(boardComment);
    }

    public void deleteBoardComment(Long boardId, Long boardCommentId, User user){
        Board board = findBoard(boardId);
        BoardComment boardComment = boardCommentRepository
                .findByBoardIdAndIdAndUser(board.getId().toString(), boardCommentId, user.getNickname())
                .orElseThrow(() -> new NotFoundBoardCommentException(BoardCommentErrorCode.NOT_FOUND_BOARDCOMMENT));
        boardCommentRepository.delete(boardComment);
    }

    @Override
    public BoardCommentReadResponseDto readBoardComment(Long boardId, Long boardCommentId) {
        findBoard(boardId);
        BoardComment boardComment = boardCommentRepository.findById(boardCommentId)
                .orElseThrow(() -> new NotFoundBoardCommentException(BoardCommentErrorCode.NOT_FOUND_BOARDCOMMENT));
        String name = boardComment.getUser();
        User user = userRepository.findByNickname(name)
            .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        String comment_count = boardCommentRepository.countByCommentId(boardCommentId).toString();
        return boardCommentEntityMapper.toBoardCommentReadResponseDto(boardComment,user,comment_count);
    }

    @Override
    public BoardCommentListResponseDto readAllBoardComment(Long boardId, int pageNum) {
        Board board = findBoard(boardId);
        PageRequest pageRequest = PageRequest.of(pageNum, 5);
        Slice<BoardComment> commentSlice = boardCommentRepository.findAllByBoardId(board.getId().toString(), pageRequest);
        List<BoardComment> comments = commentSlice.getContent();
        List<BoardCommentReadResponseDto> content = new ArrayList<>();
        for (BoardComment comment : comments) {
            String name = comment.getUser();
            User user = userRepository.findByNickname(name)
                    .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
            String boardCommentCount = boardCommentRepository.countByCommentId(comment.getId()).toString();
            BoardCommentReadResponseDto boardCommentReadResponseDto =
                    boardCommentEntityMapper.toBoardCommentReadResponseDto(comment, user, boardCommentCount);
            content.add(boardCommentReadResponseDto);
        }
        return new BoardCommentListResponseDto(content, commentSlice.hasNext());
    }

    public BoardCommentListResponseDto readAllBoardCommentByDesc(Long boardId, int pageNum) {
        Board board = findBoard(boardId);
        PageRequest pageRequest = PageRequest.of(pageNum, 5);
        Slice<BoardComment> commentSlice = boardCommentRepository
                .findAllByBoardIdOrderByCreatedAtDesc(board.getId().toString(), pageRequest);
        List<BoardComment> comments = commentSlice.getContent();
        List<BoardCommentReadResponseDto> content = new ArrayList<>();
        for (BoardComment comment : comments) {
            String name = comment.getUser();
            User user = userRepository.findByNickname(name)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
            String boardCommentCount = boardCommentRepository.countByCommentId(comment.getId()).toString();
            BoardCommentReadResponseDto boardCommentReadResponseDto =
                    boardCommentEntityMapper.toBoardCommentReadResponseDto(comment, user, boardCommentCount);
            content.add(boardCommentReadResponseDto);
        }
        return new BoardCommentListResponseDto(content, commentSlice.hasNext());
    }

    private Board findBoard(Long boardId){
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
    }
}
