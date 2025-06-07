package com.tablelog.tablelogback.domain.chat.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Str;


public class Chat {
    private String username;
    private String message;
}
