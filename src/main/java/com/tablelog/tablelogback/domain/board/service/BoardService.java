package com.tablelog.tablelogback.domain.board.service;




import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface BoardService {

    void create(BoardCreateServiceRequestDto requestDto
    , User user
    , MultipartFile multipartFile
    ) throws IOException;

    void update(BoardUpdateServiceRequestDto requestDto
            ,User user
            ,Long id
            ,MultipartFile multipartFile
    ) throws IOException;
//    TestReadResponseDto get(Long id);
//
//    List<TestReadResponseDto> getAll(int pageNumber);
}
