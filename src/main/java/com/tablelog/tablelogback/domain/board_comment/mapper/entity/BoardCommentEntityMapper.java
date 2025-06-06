package com.tablelog.tablelogback.domain.board_comment.mapper.entity;



import com.tablelog.tablelogback.domain.board.dto.service.BoardReadResponseDto;
import com.tablelog.tablelogback.domain.board.entity.Board;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board_comment.dto.service.BoardCommentReadResponseDto;
import com.tablelog.tablelogback.domain.board_comment.entity.BoardComment;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;



@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BoardCommentEntityMapper {
    @Mapping(source = "user.nickname", target = "user")
    @Mapping(source = "BoardCommentRequestDto.content",target = "content")
    @Mapping(source = "board.id",target = "boardId")
    @Mapping(source = "comment_id",target =  "comment_id")
    BoardComment toBoardComment(BoardCommentCreateServiceRequestDto BoardCommentRequestDto,
        Board board ,
        User user,
        Long comment_id
    );
    @Mapping(source = "user.nickname", target = "user")
    @Mapping(source = "user.profileImgUrl", target = "profileImgUrl")
    @Mapping(source = "boardComment.createdAt", target = "createdAt")
    BoardCommentReadResponseDto toBoardCommentReadResponseDto(BoardComment boardComment,User user);
    List<BoardCommentReadResponseDto> toBoardCommentReadResponseDtos(List<BoardComment> boardComment);
}
