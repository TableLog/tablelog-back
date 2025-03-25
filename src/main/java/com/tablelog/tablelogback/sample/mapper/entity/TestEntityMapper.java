package com.tablelog.tablelogback.sample.mapper.entity;



import com.tablelog.tablelogback.sample.dto.service.TestCreateServiceRequestDto;
import com.tablelog.tablelogback.sample.dto.service.TestReadResponseDto;
import com.tablelog.tablelogback.sample.entity.Test;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;



@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TestEntityMapper {

    Test toTest(TestCreateServiceRequestDto testRequestDto);

    TestReadResponseDto toTestReadResponseDto(Test test);

    List<TestReadResponseDto> toTestReadResponseDtos(List<Test> test);
}
