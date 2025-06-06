package com.tablelog.tablelogback.domain.chat.service.Impl;

import com.tablelog.tablelogback.domain.chat.entity.Chat;
import com.tablelog.tablelogback.domain.chat.service.ChatService;
import com.tablelog.tablelogback.domain.user.entity.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class ChatServiceImpl implements ChatService {
    private final RedisTemplate<String, String> redisTemplate2;

    // 세션 접속 시 룸 id 자동으로 생성
    public void createChatRoomId(
        String email
    ){
        String roomId = email + "-" + UUID.randomUUID().toString();
        String value = "";
        this.setRedisTemplate2(roomId,value);
    }
    // TODO 사용자가 나눈 채팅 기록 저장




    public ChatServiceImpl(RedisTemplate<String, String> redisTemplate2) {
        this.redisTemplate2 = redisTemplate2;
    }
    public void setRedisTemplate2(String key, String value) {
        redisTemplate2.opsForValue().set(key, value);
    }
}
