package com.tablelog.tablelogback.sample.service.impl;



import java.util.List;

import com.tablelog.tablelogback.sample.dto.service.TestCreateServiceRequestDto;
import com.tablelog.tablelogback.sample.dto.service.TestReadResponseDto;
import com.tablelog.tablelogback.sample.entity.Test;
import com.tablelog.tablelogback.sample.exception.NotFoundTestException;
import com.tablelog.tablelogback.sample.exception.TestErrorCode;
import com.tablelog.tablelogback.sample.mapper.entity.TestEntityMapper;
import com.tablelog.tablelogback.sample.repository.TestRepository;
import com.tablelog.tablelogback.sample.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final TestEntityMapper testEntityMapper;

    // TestCreateServiceRequestDto -> Test
    @Override
    public void create(final TestCreateServiceRequestDto testRequestDto) {
        // Mapper로 만들기
        Test test = testEntityMapper.toTest(testRequestDto);

        /* Builder로 만들기
            Test test = Test.builder()
            .name(testRequestDto.name())
            .age(testRequestDto.age())
            .build();
         */

        testRepository.save(test);
    }

    // Test -> TestCreateServiceRequestDto
    @Override
    public TestReadResponseDto get(Long id) {
        Test test = testRepository.findById(id)
            .orElseThrow(() -> new NotFoundTestException(TestErrorCode.NOT_FOUND_TEST));
        return testEntityMapper.toTestReadResponseDto(test);
    }

    // List<Test> -> List<TestCreateServiceRequestDto>
    @Override
    public List<TestReadResponseDto> getAll(int pageNumber) {
        Slice<Test> tests = testRepository.findAllBy(PageRequest.of(pageNumber, 9));
        return testEntityMapper.toTestReadResponseDtos(tests.getContent());
    }
}
