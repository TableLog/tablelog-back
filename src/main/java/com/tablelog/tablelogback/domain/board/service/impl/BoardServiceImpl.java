package com.tablelog.tablelogback.domain.board.service.impl;


import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.TestReadResponseDto;
import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.board.exception.NotFoundTestException;
import com.tablelog.tablelogback.domain.board.exception.BoardErrorCode;
import com.tablelog.tablelogback.domain.board.mapper.entity.BoardEntityMapper;
import com.tablelog.tablelogback.domain.board.repository.BoardRepository;
import com.tablelog.tablelogback.domain.board.service.BoardService;
import com.tablelog.tablelogback.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardEntityMapper boardEntityMapper;

    // TestCreateServiceRequestDto -> Test
    @Override
    public void create(final BoardCreateServiceRequestDto boardRequestDto
            , User user
            )
    {
        // Mapper로 만들기
        Board board = boardEntityMapper.toBoard(boardRequestDto
        ,user
        );

        /* Builder로 만들기
            Test test = Test.builder()
            .name(testRequestDto.name())
            .age(testRequestDto.age())
            .build();
         */

        boardRepository.save(board);
    }

    // Test -> TestCreateServiceRequestDto
//    @Override
//    public TestReadResponseDto get(Long id) {
//        Board board = boardRepository.findById(id)
//            .orElseThrow(() -> new NotFoundTestException(BoardErrorCode.NOT_FOUND_BOARD));
//        return boardEntityMapper.toTestReadResponseDto(board);
//    }
//
//    // List<Test> -> List<TestCreateServiceRequestDto>
//    @Override
//    public List<TestReadResponseDto> getAll(int pageNumber) {
//        Slice<Board> tests = boardRepository.findAllBy(PageRequest.of(pageNumber, 9));
//        return boardEntityMapper.toTestReadResponseDtos(tests.getContent());
//    }
}
