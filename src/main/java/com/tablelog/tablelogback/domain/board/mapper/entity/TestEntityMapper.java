package com.tablelog.tablelogback.domain.board.mapper.entity;



import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.TestReadResponseDto;
import com.tablelog.tablelogback.domain.board.entity.Board;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;



@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TestEntityMapper {

    Board toBoard(BoardCreateServiceRequestDto BoardRequestDto);

    TestReadResponseDto toTestReadResponseDto(Board board);

    List<TestReadResponseDto> toTestReadResponseDtos(List<Board> board);
}
