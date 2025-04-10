package com.tablelog.tablelogback.domain.board.service;




import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.user.entity.User;



public interface BoardService {

    void create(BoardCreateServiceRequestDto testRequestDto, User user);

//    TestReadResponseDto get(Long id);
//
//    List<TestReadResponseDto> getAll(int pageNumber);
}
