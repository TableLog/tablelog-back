package com.tablelog.tablelogback.domain.board.service;




import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.TestReadResponseDto;

import java.util.List;


public interface TestService {

    void create(BoardCreateServiceRequestDto testRequestDto);

    TestReadResponseDto get(Long id);

    List<TestReadResponseDto> getAll(int pageNumber);
}
