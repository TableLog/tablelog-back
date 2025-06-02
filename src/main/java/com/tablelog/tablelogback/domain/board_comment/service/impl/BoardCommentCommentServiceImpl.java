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
import com.tablelog.tablelogback.global.s3.S3Provider;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
    private final S3Provider s3Provider;
    private final String url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";
    @Value("${spring.cloud.aws.s3.bucket}")
    public String bucket;
    private final String SEPARATOR = "/";


    // TestCreateServiceRequestDto -> Test
    @Override
    public void create(final BoardCommentCreateServiceRequestDto boardCommentRequestDto,
            Long board_id
            , User user
            )throws IOException
    {
        Board board = boardRepository.findById(board_id)
                .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        BoardComment boardComment = boardCommentEntityMapper.toBoardComment(boardCommentRequestDto,board,user);
        boardCommentRepository.save(boardComment);

    }
    @Override
    public void update(final BoardCommentUpdateServiceRequestDto boardCommentRequestDto
            , User user
            , Long board_id
            , Long boardComment_id
    )throws IOException
    {
        Board board = boardRepository.findByIdAndUser(board_id,user.getNickname())
                .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        BoardComment boardComment = boardCommentRepository.findByBoardIdAndIdAndUser(board.getId().toString(),boardComment_id,user.getNickname())
                .orElseThrow(()-> new NotFoundBoardCommentException(BoardCommentErrorCode.NOT_FOUND_BOARDCOMMENT));
        boardComment.update(boardCommentRequestDto.content());
        boardCommentRepository.save(boardComment);
    }
    public void delete(Long board_id,Long boardComment_id,User user){
        Board board = boardRepository.findByIdAndUser(board_id,user.getNickname())
            .orElseThrow(()->new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        BoardComment boardComment = boardCommentRepository.findByBoardIdAndIdAndUser(board.getId().toString(),boardComment_id,user.getNickname())
                .orElseThrow(()->new NotFoundBoardCommentException(BoardCommentErrorCode.NOT_FOUND_BOARDCOMMENT));
        boardCommentRepository.delete(boardComment);
        }
    @Override
    public BoardCommentReadResponseDto getOnce(Long boardComment_id) {
        BoardComment boardComment = boardCommentRepository.findById(boardComment_id)
                .orElseThrow(()->new NotFoundBoardCommentException(BoardCommentErrorCode.NOT_FOUND_BOARDCOMMENT));
        String name = boardComment.getUser();
        User user = userRepository.findByNickname(name)
            .orElseThrow(()->new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
        return boardCommentEntityMapper.toBoardCommentReadResponseDto(boardComment,user);
    }

    @Override
    public BoardCommentListResponseDto getAll(Long boardId, int pageNumber) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        PageRequest pageRequest = PageRequest.of(pageNumber, 5); // 5개씩 가져오기
        Slice<BoardComment> commentSlice = boardCommentRepository.findAllByBoardId(board.getId().toString(), pageRequest);
        List<BoardComment> comments = commentSlice.getContent();
        List<BoardCommentReadResponseDto> content = new ArrayList<>();
        for (BoardComment comment : comments) {
            String name = comment.getUser();
            User user = userRepository.findByNickname(name)
                    .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
            BoardCommentReadResponseDto boardCommentReadResponseDto = boardCommentEntityMapper.toBoardCommentReadResponseDto(comment, user);
            content.add(boardCommentReadResponseDto);
        }
        return new BoardCommentListResponseDto(content, commentSlice.hasNext());
    }
    public BoardCommentListResponseDto getAllByDesc(Long boardId, int pageNumber) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new NotFoundBoardException(BoardErrorCode.NOT_FOUND_BOARD));
        PageRequest pageRequest = PageRequest.of(pageNumber, 5); // 5개씩 가져오기
        Slice<BoardComment> commentSlice = boardCommentRepository.findAllByBoardIdOrderByCreatedAtDesc(board.getId().toString(), pageRequest);
        List<BoardComment> comments = commentSlice.getContent();
        List<BoardCommentReadResponseDto> content = new ArrayList<>();
        for (BoardComment comment : comments) {
            String name = comment.getUser();
            User user = userRepository.findByNickname(name)
                .orElseThrow(() -> new NotFoundUserException(UserErrorCode.NOT_FOUND_USER));
            BoardCommentReadResponseDto boardCommentReadResponseDto = boardCommentEntityMapper.toBoardCommentReadResponseDto(comment, user);
            content.add(boardCommentReadResponseDto);
        }
        return new BoardCommentListResponseDto(content, commentSlice.hasNext());
    }

}
