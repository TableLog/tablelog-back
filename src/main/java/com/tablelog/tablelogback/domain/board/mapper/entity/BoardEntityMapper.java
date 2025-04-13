package com.tablelog.tablelogback.domain.board.mapper.entity;



import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.TestReadResponseDto;
import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;



@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BoardEntityMapper {
    @Mapping(source = "user.nickname", target = "user")
    @Mapping(source = "BoardRequestDto.title",target = "title")
    @Mapping(source = "BoardRequestDto.content",target = "content")
    @Mapping(source = "BoardRequestDto.category",target = "category")
    Board toBoard(BoardCreateServiceRequestDto BoardRequestDto
, User user
    );

    TestReadResponseDto toTestReadResponseDto(Board board);

    List<TestReadResponseDto> toTestReadResponseDtos(List<Board> board);
}
