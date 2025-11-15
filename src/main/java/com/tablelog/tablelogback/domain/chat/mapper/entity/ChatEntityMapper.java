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

    // unreadCount와 수신자 정보를 함께 세팅하는 default 헬퍼
    default ChatRoomLastMessageResponseDto toChatRoomLastMessageResponseDto(
            ChatRepository.LastMessageProjection projection,
            long unreadCount,
            String nickname,
            String profileImgUrl) {
        return new ChatRoomLastMessageResponseDto(
                projection.getRoomId(),
                projection.getLastMessage(),
                projection.getLastCreatedAt(),
                unreadCount,
                nickname,
                profileImgUrl
        );
    }

    // 수신자 정보를 포함한 ChatMessageServiceResponseDto 매핑
    default ChatMessageServiceResponseDto toChatMessageServiceResponseDto(
            Chat chat,
            String nickname,
            String profileImgUrl) {
        return new ChatMessageServiceResponseDto(
                chat.getId(),
                chat.getRoomId(),
                chat.getSender(),
                chat.getMessage(),
                chat.getMessageType(),
                chat.getCreatedAt(),
                nickname,
                profileImgUrl
        );
    }
}
