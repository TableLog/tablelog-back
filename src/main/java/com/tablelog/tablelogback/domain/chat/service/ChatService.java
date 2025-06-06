package com.tablelog.tablelogback.domain.chat.service;

import com.tablelog.tablelogback.domain.chat.entity.Chat;
import com.tablelog.tablelogback.domain.user.entity.User;
import org.springframework.stereotype.Service;


public interface ChatService {
    public void createChatRoomId(String email);
}
