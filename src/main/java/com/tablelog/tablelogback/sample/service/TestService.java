package com.tablelog.tablelogback.sample.service;




import com.tablelog.tablelogback.sample.dto.service.TestCreateServiceRequestDto;
import com.tablelog.tablelogback.sample.dto.service.TestReadResponseDto;
import java.util.List;


public interface TestService {

    void create(TestCreateServiceRequestDto testRequestDto);

    TestReadResponseDto get(Long id);

    List<TestReadResponseDto> getAll(int pageNumber);
}
