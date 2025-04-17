package com.tablelog.tablelogback.domain.board.mapper.dto;



import com.tablelog.tablelogback.domain.board.dto.controller.BoardCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.board.dto.controller.BoardUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardUpdateServiceRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface BoardDtoMapper {

    BoardCreateServiceRequestDto toBoardServiceRequestDto(
        BoardCreateControllerRequestDto controllerRequestDto);

    BoardUpdateServiceRequestDto toBoardUpdateServiceRequestDto(
            BoardUpdateControllerRequestDto controllerRequestDto);
}
