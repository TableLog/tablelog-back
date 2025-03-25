package com.tablelog.tablelogback.sample.mapper.dto;



import com.tablelog.tablelogback.sample.dto.controller.TestCreateControllerRequestDto;
import com.tablelog.tablelogback.sample.dto.service.TestCreateServiceRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface TestDtoMapper {

    TestCreateServiceRequestDto toTestServiceRequestDto(
        TestCreateControllerRequestDto controllerRequestDto);
}
