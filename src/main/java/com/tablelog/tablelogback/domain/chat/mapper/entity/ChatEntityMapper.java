package com.tablelog.tablelogback.domain.chat.mapper.entity;

import com.tablelog.tablelogback.domain.chat.dto.service.ChatMessageServiceRequestDto;
import com.tablelog.tablelogback.domain.chat.dto.service.ChatMessageServiceResponseDto;
import com.tablelog.tablelogback.domain.chat.dto.service.ChatRoomLastMessageResponseDto;
import com.tablelog.tablelogback.domain.chat.entity.Chat;
import com.tablelog.tablelogback.domain.chat.repository.ChatRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatEntityMapper {

    Chat toChat(ChatMessageServiceRequestDto chatMessageServiceRequestDto);

    ChatMessageServiceResponseDto toChatMessageServiceResponseDto(Chat chat);

    List<ChatMessageServiceResponseDto> toChatMessageServiceResponseDtos(List<Chat> chats);

    // 최근 메시지 프로젝션 → DTO 매핑
    ChatRoomLastMessageResponseDto toChatRoomLastMessageResponseDto(ChatRepository.LastMessageProjection projection);

    // unreadCount를 함께 세팅해야 하는 경우를 위한 default 헬퍼
    default ChatRoomLastMessageResponseDto toChatRoomLastMessageResponseDto(ChatRepository.LastMessageProjection projection, long unreadCount) {
        return new ChatRoomLastMessageResponseDto(
                projection.getRoomId(),
                projection.getLastMessage(),
                projection.getLastCreatedAt(),
                unreadCount
        );
    }
}
