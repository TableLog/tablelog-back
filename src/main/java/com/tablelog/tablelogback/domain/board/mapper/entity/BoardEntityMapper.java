package com.tablelog.tablelogback.domain.board.mapper.entity;



import com.tablelog.tablelogback.domain.board.dto.service.BoardCreateServiceRequestDto;
import com.tablelog.tablelogback.domain.board.dto.service.BoardReadResponseDto;
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
    @Mapping(source = "fileUrl",target = "image_urls")
    Board toBoard(BoardCreateServiceRequestDto BoardRequestDto, List<String> fileUrl
, User user
    );
    @Mapping(source = "board.id", target = "id")
    @Mapping(source = "profileImgUrl", target = "profileImgUrl")
    @Mapping(source = "comment_count" ,target= "comment_count")
    @Mapping(source = "like_count", target = "like_count")
    @Mapping(source = "board.createdAt", target = "createdAt")
    @Mapping(source = "user_id",target = "user_id")
    BoardReadResponseDto toReadResponseDto(Board board,String profileImgUrl,Integer comment_count,Long like_count,
        Boolean isMe,Boolean isLike,Long user_id);
//    List<BoardReadResponseDto> toBoardReadResponseDtos(
//        List<Board> boards,
//        List<User> users,
//        List<Integer> comment_counts,
//        List<Long> like_counts
//    );
}
