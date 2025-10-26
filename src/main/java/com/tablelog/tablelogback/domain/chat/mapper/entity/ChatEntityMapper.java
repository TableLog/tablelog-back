package com.tablelog.tablelogback.domain.chat.mapper.entity;

import com.tablelog.tablelogback.domain.chat.dto.service.ChatMessageServiceRequestDto;
import com.tablelog.tablelogback.domain.chat.dto.service.ChatMessageServiceResponseDto;
import com.tablelog.tablelogback.domain.chat.entity.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatEntityMapper {

    Chat toChat(ChatMessageServiceRequestDto chatMessageServiceRequestDto);

    ChatMessageServiceResponseDto toChatMessageServiceResponseDto(Chat chat);

    List<ChatMessageServiceResponseDto> toChatMessageServiceResponseDtos(List<Chat> chats);
}
