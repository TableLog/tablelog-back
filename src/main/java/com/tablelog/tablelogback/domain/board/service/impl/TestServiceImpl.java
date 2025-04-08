package com.tablelog.tablelogback.domain.board.service.impl;


import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.TestReadResponseDto;
import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.board.exception.NotFoundTestException;
import com.tablelog.tablelogback.domain.board.exception.TestErrorCode;
import com.tablelog.tablelogback.domain.board.mapper.entity.TestEntityMapper;
import com.tablelog.tablelogback.domain.board.repository.TestRepository;
import com.tablelog.tablelogback.domain.board.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final TestEntityMapper testEntityMapper;

    // TestCreateServiceRequestDto -> Test
    @Override
    public void create(final BoardCreateServiceRequestDto testRequestDto) {
        // Mapper로 만들기
        Board board = testEntityMapper.toTest(testRequestDto);

        /* Builder로 만들기
            Test test = Test.builder()
            .name(testRequestDto.name())
            .age(testRequestDto.age())
            .build();
         */

        testRepository.save(board);
    }

    // Test -> TestCreateServiceRequestDto
    @Override
    public TestReadResponseDto get(Long id) {
        Board board = testRepository.findById(id)
            .orElseThrow(() -> new NotFoundTestException(TestErrorCode.NOT_FOUND_TEST));
        return testEntityMapper.toTestReadResponseDto(board);
    }

    // List<Test> -> List<TestCreateServiceRequestDto>
    @Override
    public List<TestReadResponseDto> getAll(int pageNumber) {
        Slice<Board> tests = testRepository.findAllBy(PageRequest.of(pageNumber, 9));
        return testEntityMapper.toTestReadResponseDtos(tests.getContent());
    }
}
