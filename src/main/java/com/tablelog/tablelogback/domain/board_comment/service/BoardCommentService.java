package com.tablelog.tablelogback.domain.board_comment.service;




import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentListResponseDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentReadResponseDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.board_comment.entity.BoardComment;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.springframework.data.domain.Slice;

import java.io.IOException;
import java.util.List;


public interface BoardCommentService {

    void create(BoardCommentCreateServiceRequestDto requestDto
    , Long board_id
    , User user
    ) throws IOException;

    void update(BoardCommentUpdateServiceRequestDto requestDto
            ,User user
            ,Long board_id
            ,Long boardComment_id
    ) throws IOException;
     void delete(
        Long board_id,
         Long boardComment_id,
         User user
     ) throws IOException;
    BoardCommentReadResponseDto getOnce(Long id);
    BoardCommentListResponseDto getAll(Long boardId, int pageNumber);
}
