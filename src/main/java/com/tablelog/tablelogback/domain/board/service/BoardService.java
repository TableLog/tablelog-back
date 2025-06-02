package com.tablelog.tablelogback.domain.board.service;




import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardListResponseDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardReadResponseDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardUpdateServiceRequestDto;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Io;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface BoardService {

    void create(BoardCreateServiceRequestDto requestDto
    , User user
    , List<MultipartFile> multipartFiles
    ) throws IOException;

    void update(BoardUpdateServiceRequestDto requestDto
            ,User user
            ,Long id
            ,List<MultipartFile> multipartFiles
    ) throws IOException;
     void delete(
         Long id,
         User user
     ) throws IOException;
//    TestReadResponseDto get(Long id);
//
    BoardListResponseDto getAll(int pageNumber);
    BoardListResponseDto getAllByDesc(int pageNumber);
    BoardListResponseDto getAllByAsc(int pageNumber);
    BoardReadResponseDto getOnce(Long id);
    BoardListResponseDto getAllByUser(int pageNumber,User user);
    BoardListResponseDto getAllByDescAndUser(int pageNumber,User user);
    BoardReadResponseDto getOnceLogin(Long id,User user);
}
