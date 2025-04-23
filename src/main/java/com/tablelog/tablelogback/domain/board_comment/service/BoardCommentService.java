package com.tablelog.tablelogback.domain.board_comment.service;




import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.user.entity.User;

import java.io.IOException;


public interface BoardCommentService {

    void create(BoardCommentCreateServiceRequestDto requestDto
    , Long board_id
    , User user
    ) throws IOException;

//    void update(BoardUpdateServiceRequestDto requestDto
//            ,User user
//            ,Long id
//            ,MultipartFile multipartFile
//    ) throws IOException;
//     void delete(
//         Long id,
//         User user
//     ) throws IOException;
////    TestReadResponseDto get(Long id);
////
//    List<BoardReadResponseDto> getAll(int pageNumber);
//    BoardReadResponseDto getOnce(Long id);
}
