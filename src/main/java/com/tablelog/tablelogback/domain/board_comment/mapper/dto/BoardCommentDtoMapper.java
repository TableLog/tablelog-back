package com.tablelog.tablelogback.domain.board_comment.mapper.dto;

import com.tablelog.tablelogback.domain.board_comment.dto.controller.BoardCommentCreateControllerRequestDto;
import com.tablelog.tablelogback.domain.board_comment.dto.controller.BoardCommentUpdateControllerRequestDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentUpdateServiceRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface BoardCommentDtoMapper {

    BoardCommentCreateServiceRequestDto toBoardCommentServiceRequestDto(
        BoardCommentCreateControllerRequestDto controllerRequestDto);

    BoardCommentUpdateServiceRequestDto toBoardCommentUpdateServiceRequestDto(
            BoardCommentUpdateControllerRequestDto controllerRequestDto);
}
